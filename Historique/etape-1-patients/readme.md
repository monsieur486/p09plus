# Étape 1 — Gestion des patients

> **À propos de ce dossier.** Cette étape est une copie autonome du projet à ce stade de
> sa construction. Le code y est livré **brut, sans commentaires ni Javadoc**, afin de ne
> montrer que la structure. La version documentée se trouve dans l'étape 5.

Première étape : la gestion des fiches patients et l'interface web qui les présente.
Le socle technique est posé dès maintenant — registre de services, passerelle et
bibliothèque partagée — pour que les étapes suivantes n'aient qu'à ajouter leur module.

## Ce que fait l'application

Un praticien se connecte, consulte la liste des patients, ouvre une fiche, en crée
une nouvelle ou modifie une existante. Un patient est identifié de façon unique par son
nom, son prénom, sa date de naissance et son genre : deux fiches identiques ne peuvent
coexister.

## Modules

| Module | Port | Rôle |
|---|---|---|
| `ms-commun` | — | DTO d'API, exceptions métier, traitement uniforme des erreurs |
| `ms-eureka` | 8761 | registre de services |
| `ms-gateway` | 9000 | point d'entrée unique des API |
| `ms-webclient` | 8080 | interface web (Thymeleaf + Bootstrap 5) |
| `ms-patients` | 9100 | fiches patients — PostgreSQL 16 |

## API

| Méthode | Chemin | Rôle |
|---|---|---|
| `GET` | `/ms-patients/patients` | liste des patients |
| `GET` | `/ms-patients/patients/{id}` | fiche d'un patient |
| `POST` | `/ms-patients/patients` | création |
| `PUT` | `/ms-patients/patients/{id}` | mise à jour |

Authentification HTTP Basic (`app_user` / `app_password`). Les erreurs distinguent la
responsabilité : **4xx** la demande est en cause (`400` donnée invalide, `404` inexistante,
`409` doublon), **5xx** le service est en cause.

## Étape suivante

L'étape 2 ajoute les notes de consultation.

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
