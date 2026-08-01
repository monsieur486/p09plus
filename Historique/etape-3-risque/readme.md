# Dépistage du risque de diabète

Application de dépistage à destination des praticiens : elle regroupe les fiches patients,
les notes des consultations, et calcule à partir de ces notes un niveau de risque de
diabète. L'ensemble est découpé en microservices Spring Boot 3.5 / Java 17, orchestrés par
Docker Compose.

## Architecture

| Module | Port | Rôle |
|---|---|---|
| `ms-commun` | — | bibliothèque partagée : DTO d'API, exceptions métier, traitement uniforme des erreurs |
| `ms-eureka` | 8761 | registre de services |
| `ms-gateway` | 9000 | point d'entrée unique des API |
| `ms-webclient` | 8080 | interface web des praticiens (Thymeleaf) |
| `ms-patients` | 9100 | fiches patients — PostgreSQL 16 |
| `ms-notes` | 9200 | notes des consultations — MongoDB 8 |
| `ms-risque` | 9300 | calcul du niveau de risque |

La passerelle route les appels d'après le nom du microservice, placé en tête de l'URL :
`/ms-patients/patients/1` atteint `GET /patients/1` sur le service des patients. Les trois
microservices métier n'exposent aucun port sur l'hôte : ils ne sont joignables qu'à travers
la passerelle.

## Prérequis

- Java 17 et Docker 24+ / Docker Compose 2.40+
- Copier `dist.env` en `.env` et adapter les valeurs si des ports sont déjà occupés.
  Sans `.env`, les valeurs par défaut de `dist.env` s'appliquent.

## Démarrer

```bash
./prod-start.sh    # pile complète (bases + services + IHM), microservices répliqués 3 fois
./prod-stop.sh     # arrêt, données conservées
```

**Compter environ une minute avant que la pile réponde**, même une fois tous les
conteneurs affichés « Up ». Chaque microservice doit d'abord se déclarer auprès du
registre, puis la passerelle rafraîchir son catalogue pour savoir où router. Pendant ce
laps de temps les appels échouent en 404 : c'est attendu, il n'y a rien à corriger. Le
premier `./prod-start.sh` est bien plus long, le temps de construire les six images.

En développement, on ne conteneurise que les bases ; les applications tournent en local :

```bash
./dev-start.sh                          # bases PostgreSQL et MongoDB seules
cd ms-eureka && ./mvnw spring-boot:run  # registre à démarrer en premier
cd ms-patients && ./mvnw spring-boot:run
./dev-stop.sh                           # arrêt, données conservées
```

Déployer une mise à jour (arrêt, `git pull`, reconstruction) :

```bash
./maj.sh
```

## Construire et vérifier

Le dépôt est un projet Maven multi-module : les commandes se lancent **depuis la racine**.

```bash
./mvnw verify                  # compile, teste, et produit les rapports qualité
./mvnw clean verify site       # + site HTML dans target/site (couverture, Checkstyle, PMD, Javadoc)
./mvnw -pl ms-risque test      # tests d'un seul module
./mvnw -pl ms-risque -Dtest=EvaluationServiceTest test          # une classe
./mvnw -pl ms-risque -Dtest=EvaluationServiceTest#aucunDeclencheur_retourneNone test   # une méthode
```

Les analyses (Checkstyle, PMD, SpotBugs, couverture JaCoCo) sont **indicatives** : elles
n'interrompent pas le build, mais l'objectif reste zéro violation et 80 % de couverture.
Les tests d'intégration démarrent de vraies bases via Testcontainers, Docker doit donc être
disponible.

## Accès

| Ressource | URL | Identifiants |
|---|---|---|
| Interface web | http://localhost:8080 | `app_user` / `app_password` |
| Passerelle | http://localhost:9000 | idem, en HTTP Basic |
| Registre Eureka | http://localhost:8761 | — |
| Documentation Swagger | http://localhost:9000/swagger-ui/index.html | — |

## API

Toutes les API exigent une authentification HTTP Basic et sont appelées via la passerelle.

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/ms-patients/patients?page=0&size=10` | liste paginée des patients |
| `GET` | `/ms-patients/patients/{id}` | fiche d'un patient |
| `POST` | `/ms-patients/patients` | création d'un patient |
| `PUT` | `/ms-patients/patients/{id}` | mise à jour d'un patient |
| `GET` | `/ms-notes/patients/{id}/notes?page=0&size=10` | notes paginées d'un patient |
| `POST` | `/ms-notes/patients/{id}/notes` | ajout d'une note |
| `GET` | `/ms-risque/evaluation/{id}` | niveau de risque d'un patient |

Les deux listes sont **paginées** : `page` commence à zéro, `size` vaut 10 par défaut et
est plafonné à 100. La réponse porte le contenu et de quoi construire la navigation :

```json
{
  "contenu": [ { "id": 1, "firstName": "Jean", "lastName": "Dupont", "birthDate": "1990-05-12",
                 "gender": "M", "postalAddress": "1 rue des Lilas", "phoneNumber": "100-222-3333" } ],
  "page": 0,
  "taille": 10,
  "totalElements": 47,
  "totalPages": 5
}
```

Création d'un patient :

```http
POST /ms-patients/patients
Content-Type: application/json

{
  "firstName": "Jean",
  "lastName": "Dupont",
  "birthDate": "1990-05-12",
  "gender": "M",
  "postalAddress": "1 rue des Lilas",
  "phoneNumber": "100-222-3333"
}
```

```json
{
  "id": 5,
  "firstName": "Jean",
  "lastName": "Dupont",
  "birthDate": "1990-05-12",
  "gender": "M",
  "postalAddress": "1 rue des Lilas",
  "phoneNumber": "100-222-3333"
}
```

Toutes les erreurs partagent le même format, quel que soit le microservice appelé :

```json
{
  "timestamp": "2026-08-01T09:12:33Z",
  "microserviceName": "ms-patients",
  "path": "/patients/42",
  "errorCode": "NOT_FOUND",
  "messages": ["Aucun patient avec l'id: 42"]
}
```

Les statuts distinguent nettement la responsabilité de l'erreur :

- **4xx — la demande est en cause** : `400` donnée invalide, `404` ressource inexistante,
  `409` patient déjà enregistré. Le message métier est renvoyé pour permettre la correction.
- **5xx — le service est en cause** : `503` microservice injoignable, `500` erreur
  inattendue. Seul un message générique est renvoyé ; la cause est journalisée côté serveur.

## Règle d'évaluation du risque

Le niveau est déterminé en croisant le nombre de termes déclencheurs relevés dans les notes,
l'âge du patient et son genre. Les niveaux sont évalués du plus grave au moins grave, de
sorte qu'un nombre de déclencheurs plus élevé ne puisse jamais aboutir à un risque moindre.

| Patient | Early onset | In Danger | Borderline |
|---|---|---|---|
| Homme de moins de 30 ans | ≥ 5 | ≥ 3 | — |
| Femme de moins de 30 ans | ≥ 7 | ≥ 4 | — |
| 30 ans et plus | ≥ 8 | 6 à 7 | 2 à 5 |

Aucun déclencheur donne toujours `None`. Les jeux de données de démonstration contiennent
quatre patients, un par niveau attendu.

## Schéma des microservices

![Schéma des microservices](Mircoservices.png)
