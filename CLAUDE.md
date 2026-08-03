# CLAUDE.md

Guide pour Claude Code (claude.ai/code) dans ce dépôt.

## Structure du dépôt

Le dépôt ne contient pas un projet, mais **sa construction découpée en six étapes**, toutes
sous `Historique/`. La racine ne porte que `readme.md` (index des étapes), `.gitignore` et ce
fichier — **aucun code**.

| Dossier | Ce que l'étape apporte |
|---|---|
| `Historique/etape-1-patients` | socle (registre, passerelle, `ms-commun`), fiches patients, IHM |
| `Historique/etape-2-notes` | notes de consultation (MongoDB) |
| `Historique/etape-3-risque` | évaluation du risque de diabète |
| `Historique/etape-4-swagger` | documentation OpenAPI des trois API |
| `Historique/etape-5-complet` | le même code, commenté et documenté — **version de référence** |
| `Historique/etape-6-kubernetes` | le même projet, déployé sur Kubernetes au lieu de Docker Compose |

Chaque étape est un **projet Maven multi-module autonome** : son propre wrapper (`.mvn`,
`mvnw`), son `pom.xml` parent, son `docker-compose.yml`, ses scripts, son `config/`. Une
étape ignore les autres ; les commandes se lancent **depuis le dossier de l'étape**, jamais
depuis la racine.

Deux règles à ne jamais enfreindre :

1. Les étapes 1 à 4 sont livrées **sans commentaires ni Javadoc** (le code y est à nu, pour
   ne montrer que la structure). Seule l'étape 5 est documentée.
2. Une évolution du code doit être portée dans **toutes les étapes où le module concerné
   existe** : dépouillée dans les étapes 1 à 4, documentée dans l'étape 5. Les étapes sont
   des copies, pas des branches — rien ne les synchronise automatiquement.

   **Une exception assumée, décidée le 3 août 2026** : les corrections issues de l'analyse
   creedengo (le `final` posé sur 147 déclarations, et les correctifs Sonar qui les
   accompagnent) n'existent que dans l'étape 6. Le code métier des étapes 1 à 5 en diverge
   donc volontairement. Ne pas « réaligner » les étapes sans demander : l'écart est voulu,
   pas un oubli.

L'écart entre deux étapes voisines est exactement ce que la seconde annonce : l'étape 4 est
l'étape 3 plus Swagger (dépendances `springdoc`, `DocumentationApiConfiguration`,
annotations des contrôleurs, bloc `springdoc:` de la passerelle) ; l'étape 5 est l'étape 4
plus la documentation ; l'étape 6 est l'étape 5 déployée autrement — voir la section qui lui
est consacrée plus bas.

## Commandes

Depuis un dossier d'étape — ici `Historique/etape-5-complet` :

```bash
./prod-start.sh                   # pile complète en conteneurs (build ; réplicas lus dans le .env)
./prod-stop.sh                    # arrêt, données conservées
./dev-start.sh                    # bases PostgreSQL et MongoDB seules
./dev-stop.sh                     # arrêt + suppression des volumes
./maj.sh                          # arrêt, git pull, reconstruction

./mvnw verify                     # compile, teste, produit les rapports qualité
./mvnw clean verify site          # + site HTML dans target/site
./mvnw -pl ms-risque test         # tests d'un seul module
./mvnw -pl ms-risque -Dtest=EvaluationServiceTest test                     # une classe
./mvnw -pl ms-risque -Dtest=EvaluationServiceTest#aucunDeclencheur_retourneNone test
```

Les commandes Maven valent pour toutes les étapes. Les scripts de déploiement, non :
l'étape 6 remplace `prod-start.sh` / `prod-stop.sh` / `maj.sh` par `k8s-start.sh` et
`k8s-stop.sh`, et lui seule porte `sonar-scan.sh` (voir sa section plus bas).

En développement local, lancer `ms-eureka` en premier : les clients pointent sur
`http://localhost:8761/eureka` dans le profil par défaut.

Le nombre d'instances des services métier se règle par `REPLICA_MS_*` dans le `.env`, via
`deploy.replicas` du `docker-compose.yml` : c'est **Compose** qui lit ce fichier. Ne jamais
repasser par un `--scale` dans `prod-start.sh` — un shell ne lit pas le `.env`, et les
valeurs saisies seraient ignorées en silence.

Checkstyle, PMD, SpotBugs et JaCoCo (seuil 80 %) sont configurés dans le `pom.xml` parent et
lisent `config/` via `${maven.multiModuleProjectDirectory}/config`. Ils sont **indicatifs** :
ils n'interrompent pas le build. Les `*ApplicationTests` de `ms-patients` et `ms-notes`
démarrent de vraies bases par Testcontainers — **Docker doit être disponible** pour `verify`.

## Ports

| Service | Port | Rôle |
|---|---|---|
| ms-eureka | 8761 | registre de services |
| ms-gateway | 9000 | seul point d'entrée des API |
| ms-webclient | 8080 | IHM Thymeleaf (seule UI exposée) |
| ms-patients | 9100 | fiches patients — PostgreSQL 16 |
| ms-notes | 9200 | notes de consultation — MongoDB 8 |
| ms-risque | 9300 | calcul du niveau de risque |

Les étapes 1 à 5 utilisent les mêmes ports et les mêmes noms de conteneurs : n'en faire
tourner qu'une à la fois. L'étape 6 garde ces ports **à l'intérieur du cluster** et n'en
publie aucun : seule l'IHM est jointe, par l'Ingress `p09plus.local`.

Hors application, l'étape 6 publie en développement un serveur **SonarQube sur 9001** — pas
9000, déjà pris par la passerelle en local.

Swagger agrégé (étapes 4 et 5) : `http://localhost:9000/swagger-ui/index.html`. À l'étape 6,
après `kubectl port-forward service/ms-gateway 9000:9000`.

## Architecture d'une étape

### Chemin d'une requête

```
navigateur → ms-webclient (Thymeleaf, formLogin/session)
           → RestTemplate + Basic Auth → ms-gateway:9000
           → routage par découverte Eureka → ms-patients | ms-notes | ms-risque
```

La passerelle n'a **aucune route déclarée** : elle s'appuie sur `discovery.locator` avec
`lower-case-service-id` et le filtre `StripPrefix=1`. Le premier segment de l'URL est donc le
`spring.application.name` du service cible, retiré avant transmission :

- `/ms-patients/patients/{id}` → `ms-patients` `GET /patients/{id}`
- `/ms-notes/patients/{id}/notes` → `ms-notes` `GET /patients/{id}/notes`
- `/ms-risque/evaluation/{id}` → `ms-risque` `GET /evaluation/{id}`

Conséquence : **renommer un `spring.application.name` casse toutes les URLs**, côté
`ms-webclient` (chaînes construites dans les contrôleurs) comme côté documentation
(`DocumentationApiConfiguration` déclare `Server().url("/" + nomDuMicroservice)` pour que le
« Try it out » de Swagger passe bien par la passerelle).

`ms-risque` est le seul service à appeler ses pairs **en direct via OpenFeign**
(`@FeignClient(name = "ms-patients")`), sans passer par la passerelle — la résolution se fait
par Eureka.

### Le module partagé

`ms-commun` est une bibliothèque (pas une application : ni `@SpringBootApplication` ni
`spring-boot-maven-plugin`). Elle porte les DTO d'API (`PatientDto`, `NoteDto`,
`PatientForm`), les exceptions métier, le `GlobalExceptionHandler` produisant un
`ErrorResponse` uniforme, `SecuriteApiConfiguration` et — à partir de l'étape 4 —
`DocumentationApiConfiguration`. Ces configurations ne sont pas scannées : chaque application
les charge explicitement par `@Import` dans sa classe `Ms*Application`.

### Chaîne d'authentification

Une **unique paire d'identifiants** (`APP_USERNAME` / `APP_PASSWORD`, défaut `app_user` /
`app_password`) sert partout, propagée par les variables d'environnement du
`docker-compose.yml` :

- `ms-webclient` : `formLogin` + session ; ses appels sortants sont signés par un
  `BasicAuthenticationInterceptor` posé sur le `RestTemplate` (`GatewaySecurityConfiguration`).
- `ms-patients` / `ms-notes` / `ms-risque` : `httpBasic`, CSRF désactivé,
  `InMemoryUserDetailsManager` alimenté par `security.app-user.*`. Seul `/actuator/**` est en
  `permitAll` — plus `/v3/api-docs/**` à partir de l'étape 4.
- `ms-risque` → pairs : `FeignSecurityConfiguration` ajoute l'en-tête `Authorization: Basic` ;
  `FeignRetryConfiguration` autorise 3 tentatives (backoff 100 ms → 1 s).
- `ms-gateway` et `ms-eureka` n'ont **aucune sécurité** : la passerelle relaie l'en-tête
  `Authorization` tel quel.

Changer l'identifiant applicatif impose de le changer dans les 4 services + l'IHM, via `.env`
uniquement (jamais en dur).

### Persistance et migrations

- `ms-patients` : JPA avec `ddl-auto: validate` — **le schéma n'est jamais généré par
  Hibernate**. Toute modification de l'entité `Patient` exige un nouveau fichier dans
  `src/main/resources/db/changelog/`, référencé depuis `master.xml` (Liquibase).
- En conteneurs, les migrations Liquibase sont appliquées par le service dédié
  `ms-patients-migration`, dont les instances de `ms-patients` attendent le succès
  (`condition: service_completed_successfully`). Liquibase ne protège pas la création de sa
  propre table d'historique : plusieurs instances lancées ensemble sur une base vierge se
  la disputent et l'une d'elles meurt. Mongock, lui, pose un verrou — `ms-notes` n'a pas
  besoin d'un tel préalable.
- `ms-notes` : MongoDB, migrations par **Mongock** (`@ChangeUnit` du package `dbchangelogs`,
  scanné via `mongock.migration-scan-package`). Le changelog insère les notes de démo si la
  collection est vide.
- Les fixtures des deux bases sont alignées sur les 4 cas de test métier (`TestNone`,
  `TestBorderline`, `TestInDanger`, `TestEarlyOnset`) — les modifier fait diverger les
  résultats de l'évaluation du risque.

### Règle métier

`EvaluationService` compte les occurrences de **11 termes déclencheurs**
(`ConstantesRisque.TERMES_DECLENCHEURS`, en français, comparaison en minuscules) dans les
notes du patient, puis croise ce compteur avec l'âge (pivot 30 ans) et le genre pour renvoyer
`None` / `Borderline` / `In Danger` / `Early onset`. Les niveaux sont évalués du plus grave au
moins grave. Le décompte porte sur **toutes** les notes du patient : un dossier lu
partiellement sous-estimerait le risque. C'est le cœur métier du projet : toute modification
doit rester vérifiable sur les 4 patients de fixture.

## L'étape 6 : Kubernetes

L'étape 6 porte le **même code métier que l'étape 5**, déployé sur un cluster minikube. Elle
n'a plus ni `prod-start.sh`, ni `prod-stop.sh`, ni `maj.sh` : son `docker-compose.yml` est
réduit aux deux bases et au serveur d'analyse, pour le seul `dev-start.sh`. Le déploiement
passe par `./k8s-start.sh` et `./k8s-stop.sh` (`--purge` pour effacer les volumes), et les
manifestes de `k8s/`.

Ce qu'il faut savoir avant d'y toucher :

- **Les clients Eureka s'annoncent par leur IP** — `eureka.instance.prefer-ip-address: true`
  dans les `application-docker.yml` de `ms-gateway`, `ms-patients`, `ms-notes` et
  `ms-risque`. Le DNS d'un cluster ne résout que les Services : une instance enregistrée sous
  son nom de pod provoque un `SERVFAIL` à chaque appel de la passerelle. **Ne jamais retirer
  ce réglage.**
- **Les images sont construites dans le démon Docker de minikube** (`eval "$(minikube
  docker-env)"`), sous le tag figé `p09plus/<module>:local` et `imagePullPolicy: Never`. Le
  tag ne changeant jamais, `kubectl apply` ne redéploie rien après une reconstruction :
  `k8s-start.sh` fait un `rollout restart` des déploiements qui préexistaient.
- **Le Job de migration est supprimé puis réappliqué** à chaque démarrage : un Job terminé
  n'est pas rejoué par `apply`, et un changelog Liquibase ajouté depuis resterait lettre
  morte. Les instances de `ms-patients` attendent son passage par un `initContainer` qui
  sonde la table d'historique — Kubernetes n'a pas d'équivalent du `depends_on: condition`.
- **Après un `rollout restart`, le catalogue Eureka garde une à deux minutes les instances
  disparues** : la passerelle route vers des pods morts et une requête sur *n* échoue en 500.
  C'est transitoire, il n'y a rien à corriger — le vérifier avant de diagnostiquer un bug.
- Les identifiants viennent du Secret `k8s/00-secrets.yaml`, pas du `.env` ; le nombre
  d'instances est écrit dans le champ `replicas` de chaque Deployment — 2 pour `ms-patients`
  et `ms-notes`, 3 pour `ms-risque` qui porte le calcul.
- L'IHM est le seul objet exposé, par un Ingress sur `p09plus.local` — qui suppose une entrée
  dans `/etc/hosts`. Le reste s'atteint par `kubectl port-forward`.
- **Le cluster est borné à 8 Gio et 8 cœurs**, sinon il assèche le poste. Le `--cpus` de
  `minikube start` n'étant pas toujours appliqué, la limite processeur est posée par
  `docker update --cpus=8 minikube` — à refaire après un `minikube delete`, mais pas après un
  simple `stop` ; elle se change à chaud, contrairement à la mémoire. Le nœud continue
  d'annoncer les 16 Gio de l'hôte : l'ordonnanceur raisonne donc sur une mémoire qu'il n'a
  pas, et c'est le noyau qui arbitre en cas de dépassement. Mesuré sur 1000 requêtes, jamais
  un échec : 51 s à 8 cœurs, 95 s à 6. La pile 3-3-5 tient aussi ; 5-5-5 porte les `limits`
  du nœud à 80 %.

## Conventions du code

- Indentation **4 espaces**, Lombok partout (`@RequiredArgsConstructor` pour l'injection par
  constructeur, jamais `@Autowired` sur champ).
- Packages par couche sous `com.mr486.<artifactid sans tiret>` : `configuration`, `controller`,
  `service`, `repository`, `model`, `mapper`, `client`, `dto`, `exception`.
- Français pour les messages d'erreur, les libellés Swagger et les commentaires ; anglais pour
  les identifiants Java.
- DTO d'entrée suffixés `Form` ; les DTO d'API vivent dans `ms-commun`, pas dupliqués.
- Erreurs : `ErrorResponse` uniforme (`timestamp`, `path`, `errorCode`, `microserviceName`,
  `messages`). **4xx** la demande est en cause, **5xx** le service.
- Config : un `application.yml` (hôtes `localhost`) et un `application-docker.yml` (hôtes =
  noms de services compose) par module, activés par `SPRING_PROFILES_ACTIVE=docker`. Toute
  valeur sensible passe par `${VAR:defaut}` et
  `spring.config.import: optional:file:.env[.properties]`.

## Pièges connus

- `.env` est ignoré par git ; `dist.env` est le gabarit à copier, dans **chaque étape sauf
  la sixième** — celle-ci tire ses identifiants de `k8s/00-secrets.yaml`, et son
  développement local se contente des valeurs par défaut.
- Les `Dockerfile` lancent `mvn -pl <module> -am package -DskipTests` : le build Docker ne
  détecte pas une régression de test.
- Le contexte de build étant la racine de l'étape, chaque `Dockerfile` ne doit recopier que
  les `pom.xml` des modules **présents dans cette étape** — sinon le build échoue sur un
  `not found`. C'est le premier point à vérifier en ajoutant une étape.
- Une étape se valide sans la construire : `docker compose --profile fullstack config`
  résout le `.env` et signale les erreurs de composition. À l'étape 6, l'équivalent est
  `kubectl apply --dry-run=client -f k8s/`.
- `ms-webclient` ne s'enregistre pas auprès d'Eureka et ne connaît que `app.gateway.base-url`.
- Compter environ une minute après `prod-start.sh` avant que la pile réponde : les services
  doivent se déclarer auprès du registre, puis la passerelle rafraîchir son catalogue. Les 404
  observés pendant ce laps de temps sont attendus.
- **Les règles creedengo ne figurent dans aucun profil qualité livré par SonarQube.** Sans
  le profil « creedengo way » que crée `sonar-scan.sh` (hérité de « Sonar way », complété du
  dépôt `creedengo-java`, promu par défaut), l'analyse se déroule normalement et ne remonte
  pas une seule remarque d'écoconception — l'absence de résultat ressemble alors à un code
  irréprochable. Autres pièges de la mise en service : le tag de l'image Community Build
  porte le numéro de construction (`26.6.0.123539-community`, et non `26.6.0-community`), et
  le mot de passe administrateur doit satisfaire la politique du serveur (majuscule, chiffre,
  longueur), faute de quoi l'API répond 400 sans que rien d'autre échoue.
- Les commandes de l'agent tournent sous **zsh**, qui applique les modificateurs d'expansion :
  `docker build -t "p09plus/$m:local"` produit le tag `p09plus/ms-patientsocal` (`$m:l` = « en
  minuscules »). Toujours écrire `"p09plus/${m}:local"`. L'erreur est silencieuse — le build
  réussit et le tag attendu continue de désigner l'image précédente.
