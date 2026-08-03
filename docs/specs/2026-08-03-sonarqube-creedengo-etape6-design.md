# SonarQube et les règles creedengo dans l'étape 6

> Conception validée le 3 août 2026. Portée : `Historique/etape-6-kubernetes` **uniquement**.

## Le besoin

Mesurer l'empreinte écologique du code de l'étape 6, puis l'ajuster. L'outil retenu est
**SonarQube**, doté des règles **creedengo** (ex-ecoCode, Green Code Initiative), lancé en
développement local à côté des deux bases de données — le déploiement Kubernetes de l'étape
n'est pas concerné : Sonar est un outil de fabrication, pas un composant de l'application.

## Ce qui est décidé

### 1. Portée : l'étape 6, et elle seule

Le dépôt impose normalement qu'une évolution du code métier soit portée dans toutes les
étapes où le module existe. **Cette règle est ici volontairement suspendue** : l'outillage
comme les corrections issues de creedengo restent dans l'étape 6. Les étapes 1 à 5
divergeront donc du code corrigé — divergence assumée, à rattraper plus tard s'il y a lieu.

### 2. Le serveur : une image dérivée

`sonar/Dockerfile` part de `sonarqube:26.6.0.123539-community` et y dépose le greffon
`creedengo-java-plugin` **2.2.0** dans `extensions/plugins`.

Le choix des versions n'est pas libre : creedengo 2.2.0 annonce la compatibilité avec
SonarQube 26.1 à 26.6. Le tag `26.6.0.123539-community` est donc le plus récent que la
matrice couvre — `26.7`, publié depuis, sort du domaine testé. Les deux versions sont figées,
comme tous les tags d'images du dépôt. Attention à la forme du tag : les images Community
Build portent le numéro de construction, `26.6.0-community` seul n'existe pas.

Trois conséquences de forme :

- **Port hôte 9001**, et non 9000 : `ms-gateway` occupe déjà 9000 en développement local.
- **Aucun volume monté sur `extensions/`** : un volume persistant y figerait la version du
  greffon livrée par l'image, et une montée de version resterait sans effet visible. Seuls
  `data/` et `logs/` sont persistés.
- La base **H2 embarquée** de l'image suffit : l'instance est locale et jetable. Si une
  version future de SonarQube refusait de démarrer sans base externe, le repli est un
  service PostgreSQL dédié (`sonar-db`), distinct de `patients-db`.

Le service est démarré par `dev-start.sh`, avec les bases — au prix d'environ 1,5 Gio de
mémoire et d'une minute de démarrage supplémentaires à chaque session de développement.
Elasticsearch, qu'embarque SonarQube, exige par ailleurs `vm.max_map_count ≥ 524288` :
`dev-start.sh` vérifie le réglage et affiche la commande à passer s'il est insuffisant,
sans jamais élever ses privilèges lui-même.

### 3. L'analyse : un script idempotent

`sonar-scan.sh` est le point d'entrée unique, dans la famille de `dev-start.sh` et
`k8s-start.sh`. Il enchaîne, chaque étape étant sans effet si elle est déjà faite :

1. attente de la disponibilité de SonarQube ;
2. changement du mot de passe administrateur au premier passage ;
3. génération d'un jeton, conservé dans `.sonar-token` (ignoré par git) — SonarQube 26
   n'accepte plus l'authentification par mot de passe pour l'analyse ;
4. création du profil qualité **« creedengo way »**, hérité de « Sonar way » et complété des
   règles du dépôt `creedengo-java`, puis promu profil par défaut du langage Java. Cette
   étape est indispensable : les règles creedengo ne figurent dans aucun profil livré ;
5. `./mvnw verify sonar:sonar`.

Côté `pom.xml` parent : `sonar-maven-plugin` figé en version dans `pluginManagement`, et deux
propriétés — `sonar.projectKey` et `sonar.coverage.jacoco.xmlReportPaths`. Sans la seconde,
SonarQube afficherait 0 % de couverture alors que JaCoCo produit déjà le rapport.

### 4. L'ajustement du code : arbitré, pas subi

creedengo-java porte 18 règles. Les remontées sont classées en trois tas :

| Tas | Traitement |
|---|---|
| À corriger | correction par petits diffs, relus un par un |
| Faux positif | `@SuppressWarnings` + commentaire justificatif |
| **En conflit avec le style maison** | arbitré explicitement — le style maison l'emporte |

Les remarques écartées ne sont **jamais** neutralisées depuis l'IHM de SonarQube : elles le
sont dans le code, par `@SuppressWarnings("<dépôt>:<règle>")` surmonté du commentaire qui
explique pourquoi. Vérifié : le mécanisme fonctionne pour les règles d'un greffon tiers comme
creedengo, pas seulement pour les règles natives. Deux avantages sur le statut « Accepté » du
serveur — la justification se lit dans le fichier concerné, et elle est versionnée, donc elle
survit à la purge du volume Docker. L'annotation est posée au plus près (classe pour un DTO
dont tous les champs sont visés, champ pour un `@Value`, méthode pour un paramètre de
lambda), afin de ne pas masquer les occurrences futures.

Le troisième tas n'est pas théorique. `AvoidUsageOfStaticCollections` signalera
`ConstantesRisque.TERMES_DECLENCHEURS`, qui est précisément ce que les conventions du projet
imposent (constantes de domaine en `public static final`). De même,
`AvoidMultipleIfElseStatement` visera `EvaluationService`, dont la cascade de conditions est
la traduction directe de la règle métier et se lit mieux ainsi.

**Une règle creedengo ne prime pas sur les conventions du projet.** L'objectif n'est pas
d'obtenir zéro remontée, mais de corriger ce qui a un effet réel — et de savoir dire pourquoi
le reste demeure.

Les corrections ne peuvent pas être conçues avant d'avoir vu le rapport : elles sont
présentées classées, et appliquées après accord.

#### Ce qu'a donné la première analyse

205 remontées, dont 196 creedengo, très inégalement réparties :

| Règle | Occ. | Décision |
|---|---|---|
| `GCI82` — variable jamais réassignée | 192 + 31 | `final` appliqué sur 178 ; 47 impossibles |
| `GCI76` — collections statiques | 2 | faux positifs (`List.of(…)` immuables) |
| `GCI67` — `++i` plutôt que `i++` | 1 | appliqué |
| `GCI2` — cascade de `if-else` | 1 | faux positif (comparaisons `>=`) |

Le choix retenu sur GCI82 a été de **l'appliquer partout**, malgré un gain énergétique nul —
`final` sur une variable locale ne change pas le bytecode. Quarante-sept emplacements ne
peuvent pourtant pas le recevoir, pour des raisons de compilation et non de style : 38 champs
(DTO Lombok `@Data @Builder @NoArgsConstructor`, où un champ `final` non initialisé ne
compile pas ; champs `@Value`, que Spring doit pouvoir assigner), 7 paramètres de lambda et
2 composants de `record`, qui n'admettent aucun modificateur.

Comportement de la règle à retenir : **GCI82 remonte par vagues**. Dès qu'un paramètre d'une
signature reçoit `final`, le passage suivant signale les autres paramètres de la même
signature — 192 au premier tour, 23 au deuxième, 8 au troisième. Corriger puis relancer
l'analyse, en boucle jusqu'au point fixe, est la seule marche possible ; une passe unique
donne un résultat partiel et d'apparence bâclée (une même méthode à moitié annotée).

Les règles « Sonar way » héritées ont remonté 9 défauts, dont 7 corrigés (import inutilisé,
`LocalDateTime.now()` sans zone, accès à une constante par une sous-interface, littéraux de
mois, lambdas d'assertion). Les 2 restants concernent le type de retour des gestionnaires
hérités de `ResponseEntityExceptionHandler` : les annoter `@Nullable` satisferait le contrat
Spring mais décrirait faussement un code qui ne renvoie jamais `null`.

**État final, mesuré : 205 remontées ramenées à 0.** 153 par correction du code, les 52
autres par `@SuppressWarnings` motivée — 47 GCI82 sans correction possible, 2 GCI76 et 1 GCI2
faux positifs, 2 S2638 refusées sciemment. Vingt-quatre annotations y suffisent, chacune
surmontée de sa justification. Un rapport vide n'est pas ici un rapport aveugle : c'est la
condition pour qu'une remarque nouvelle saute aux yeux. `verify` reste vert : 34 tests,
0 violation Checkstyle, 0 SpotBugs, PMD inchangé.

Dernier écart à connaître : **SonarQube ignore les exclusions déclarées pour JaCoCo** dans le
`pom.xml` et affiche donc une couverture bien inférieure (29,9 %) à celle du rapport Maven.
Les deux ne mesurent pas le même périmètre ; c'est celui de Maven, restreint au code qui
décide, qui fait foi.

### 5. Documentation

`readme.md` de l'étape 6 (le service, le script, le prérequis `sysctl`), `.gitignore`
(`.sonar-token`), et le `CLAUDE.md` racine — section « étape 6 », tableau des ports, et
mention de la divergence assumée avec les étapes 1 à 5.

## Ce qui ne change pas

Le code métier reste celui de l'étape 5 tant que les corrections ne sont pas validées. Les
analyses existantes — Checkstyle, PMD, SpotBugs, JaCoCo — sont conservées telles quelles :
SonarQube s'y ajoute, ne les remplace pas. Le déploiement Kubernetes n'est pas touché.
