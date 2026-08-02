# Étape 2 — Ajout des notes

> **À propos de ce dossier.** Cette étape est une copie autonome du projet à ce stade de
> sa construction. Le code y est livré **brut, sans commentaires ni Javadoc**, afin de ne
> montrer que la structure. La version documentée se trouve dans l'étape 5.

Deuxième étape : les notes rédigées par les praticiens viennent enrichir le dossier du
patient. Elle reprend l'étape 1 à l'identique et lui ajoute le module `ms-notes`, adossé à
une base documentaire — le format libre d'une note se prête mal à un schéma relationnel.

## Ce que l'étape ajoute

La fiche d'un patient présente désormais ses notes, de la plus récente à la plus ancienne,
avec un formulaire d'ajout. Le reste est inchangé.

## Modules

| Module | Port | Rôle |
|---|---|---|
| `ms-commun` | — | DTO d'API, exceptions métier, traitement uniforme des erreurs |
| `ms-eureka` | 8761 | registre de services |
| `ms-gateway` | 9000 | point d'entrée unique des API |
| `ms-webclient` | 8080 | interface web (Thymeleaf + Bootstrap 5) |
| `ms-patients` | 9100 | fiches patients — PostgreSQL 16 |
| `ms-notes` | 9200 | **notes de consultation — MongoDB 8** |

## API ajoutée

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/ms-notes/patients/{id}/notes` | notes d'un patient |
| `POST` | `/ms-notes/patients/{id}/notes` | ajout d'une note |

Seul le contenu rédigé transite : l'identifiant technique, le patient rattaché et la date
d'enregistrement restent internes au service.

## Étape suivante

L'étape 3 ajoute le calcul du risque de diabète à partir de ces notes.

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
./mvnw clean verify site
```

Une fois le site produit, ouvrir **`documentation-projet.html`** à la racine de l'étape :
cette page rassemble les liens vers le rapport agrégé et vers le site de chaque
microservice (Javadoc, code source croisé, Checkstyle, PMD, couverture, résultats des
tests). Les liens restent vides tant que la commande n'a pas tourné : `target/` n'est pas
versionné.
