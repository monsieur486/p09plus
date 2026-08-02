# Étape 3 — Ajout du calcul de risque

> **À propos de ce dossier.** Cette étape est une copie autonome du projet à ce stade de
> sa construction. Le code y est livré **brut, sans commentaires ni Javadoc**, afin de ne
> montrer que la structure. La version documentée se trouve dans l'étape 5.

Troisième étape : l'évaluation du risque de diabète. Elle reprend l'étape 2 et lui ajoute
`ms-risque`, qui ne possède pas de base — il interroge les deux autres services et croise
leurs données.

## Ce que l'étape ajoute

La fiche patient affiche un niveau de risque, calculé en croisant le nombre de termes
déclencheurs relevés dans les notes, l'âge du patient et son genre.

| Patient | Early onset | In Danger | Borderline |
|---|---|---|---|
| Homme de moins de 30 ans | ≥ 5 | ≥ 3 | — |
| Femme de moins de 30 ans | ≥ 7 | ≥ 4 | — |
| 30 ans et plus | ≥ 8 | 6 à 7 | 2 à 5 |

Aucun déclencheur donne toujours `None`. Les niveaux sont évalués du plus grave au moins
grave, de sorte qu'un nombre de déclencheurs plus élevé ne puisse jamais aboutir à un
risque moindre. Les quatre patients de démonstration couvrent un niveau chacun.

## Modules

| Module | Port | Rôle |
|---|---|---|
| `ms-commun` | — | DTO d'API, exceptions métier, traitement uniforme des erreurs |
| `ms-eureka` | 8761 | registre de services |
| `ms-gateway` | 9000 | point d'entrée unique des API |
| `ms-webclient` | 8080 | interface web (Thymeleaf + Bootstrap 5) |
| `ms-patients` | 9100 | fiches patients — PostgreSQL 16 |
| `ms-notes` | 9200 | notes de consultation — MongoDB 8 |
| `ms-risque` | 9300 | **évaluation du risque** |

## API ajoutée

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/ms-risque/evaluation/{id}` | niveau de risque d'un patient |

Ce service appelle `ms-patients` et `ms-notes` par le registre, sans passer par la
passerelle. Il compte les termes déclencheurs sur **toutes** les notes du patient : un
dossier lu partiellement sous-estimerait le risque. Un service injoignable produit un `503`
explicite plutôt qu'une erreur opaque.

## Étape suivante

Les trois API sont en place, mais rien ne les décrit : l'étape 4 leur ajoute une
documentation OpenAPI, consultable et essayable depuis la passerelle.

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
rafraîchir son catalogue. Les 404 observés pendant ce laps de temps sont attendus.

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
