# Étape 4 — Documentation de l'API

> **À propos de ce dossier.** Cette étape est une copie autonome du projet à ce stade de
> sa construction. Le code y est livré **brut, sans commentaires ni Javadoc**, afin de ne
> montrer que la structure. La version documentée se trouve dans l'étape 5.

Quatrième étape : la documentation des API. Elle reprend l'étape 3 sans rien changer au
métier et donne à chaque service son contrat OpenAPI, les trois étant réunis dans une
interface Swagger unique servie par la passerelle.

## Ce que l'étape ajoute

Une seule interface Swagger donne accès aux contrats des trois microservices. Le sélecteur
en haut de page bascule de l'un à l'autre.

| Ressource | URL |
|---|---|
| Interface Swagger | http://localhost:9000/swagger-ui/index.html |
| Contrat de `ms-patients` | http://localhost:9000/ms-patients/v3/api-docs |
| Contrat de `ms-notes` | http://localhost:9000/ms-notes/v3/api-docs |
| Contrat de `ms-risque` | http://localhost:9000/ms-risque/v3/api-docs |

Le bouton **Authorize** attend le compte HTTP Basic (`app_user` / `app_password`) : sans
lui, les appels d'essai retournent un `401`.

## Comment la documentation est câblée

| Où | Quoi |
|---|---|
| `pom.xml` des services | dépendance `springdoc-openapi-starter-webmvc-api` ; la passerelle prend la variante `webflux-ui`, qui sert l'interface |
| `ms-commun` — `DocumentationApiConfiguration` | déclare le schéma de sécurité `basicAuth` et le serveur du contrat |
| `ms-commun` — `SecuriteApiConfiguration` | ouvre `/v3/api-docs/**` : un contrat protégé laisserait le sélecteur vide |
| chaque contrôleur | `@OpenAPIDefinition` et `@SecurityRequirement` sur la classe, `@Tag` sur chaque opération |
| `ms-gateway` — `application.yml` | liste les contrats présentés dans le sélecteur |

Le point délicat tient au routage : la passerelle reconnaît le microservice au premier
segment de l'URL, puis le retire avant de transmettre. Un contrat qui déclarerait le
serveur par défaut ferait donc porter les appels d'essai directement sur le service, en
court-circuitant la passerelle — et l'authentification échouerait. Chaque service déclare
pour cette raison son propre nom comme serveur, lu depuis `spring.application.name` plutôt
qu'écrit en dur : la classe est partagée telle quelle par les trois modules.

## Modules

| Module | Port | Rôle |
|---|---|---|
| `ms-commun` | — | DTO d'API, exceptions métier, traitement uniforme des erreurs, socle de documentation |
| `ms-eureka` | 8761 | registre de services |
| `ms-gateway` | 9000 | point d'entrée unique des API, **interface Swagger agrégée** |
| `ms-webclient` | 8080 | interface web (Thymeleaf + Bootstrap 5) |
| `ms-patients` | 9100 | fiches patients — PostgreSQL 16 |
| `ms-notes` | 9200 | notes de consultation — MongoDB 8 |
| `ms-risque` | 9300 | évaluation du risque |

## API complète

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/ms-patients/patients` | liste des patients |
| `GET` | `/ms-patients/patients/{id}` | fiche d'un patient |
| `POST` | `/ms-patients/patients` | création d'un patient |
| `PUT` | `/ms-patients/patients/{id}` | mise à jour d'un patient |
| `GET` | `/ms-notes/patients/{id}/notes` | notes d'un patient |
| `POST` | `/ms-notes/patients/{id}/notes` | ajout d'une note |
| `GET` | `/ms-risque/evaluation/{id}` | niveau de risque d'un patient |

## Étape suivante

Le code est complet et fonctionnel, mais toujours livré à nu : l'étape 5 le reprend
commenté et documenté.

## Prérequis

- Java 17 et Docker 24+ / Docker Compose 2.40+
- Copier `dist.env` en `.env` si des ports sont déjà occupés ; sans ce fichier, les
  valeurs par défaut de `dist.env` s'appliquent.

## Démarrer

```bash
./prod-start.sh    # pile complète en conteneurs
./prod-stop.sh     # arrêt, données conservées
```

**Compter environ une minute avant que la pile réponde**, même une fois les conteneurs
affichés « Up » : les services doivent se déclarer auprès du registre, puis la passerelle
rafraîchir son catalogue. Les 404 observés pendant ce laps de temps sont attendus — et
l'interface Swagger reste vide tant que les contrats ne sont pas joignables.

En développement, seules les bases sont conteneurisées :

```bash
./dev-start.sh                          # bases de données seules
./mvnw -pl ms-eureka spring-boot:run    # registre à démarrer en premier
```

## Construire

Projet Maven multi-module : les commandes se lancent **depuis la racine de l'étape**.

```bash
./mvnw verify          # compile, teste et produit les rapports qualité
./mvnw clean verify site site:stage
```

Le but de `site:stage` est de rassembler les rapports des modules dans
`target/staging` : sans lui, le menu « Modules » du rapport agrégé renvoie vers des
pages absentes, chaque module écrivant dans son propre `target/site`.

Une fois le site assemblé, ouvrir **`target/staging/index.html`** : ce rapport agrégé
donne accès, par son menu « Modules », au site de chaque microservice — Javadoc, code
source croisé, Checkstyle, PMD, couverture JaCoCo et résultats des tests. Le dossier
`target/` n'étant pas versionné, la page n'existe qu'après la commande ci-dessus.
