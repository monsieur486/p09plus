# Dépistage du risque de diabète — construction pas à pas

Ce dépôt ne contient pas un projet, mais sa construction, découpée en six étapes. Chaque
dossier de `Historique/` est un projet Maven autonome, qui se démarre et se teste seul.

| Étape | Ce qu'elle apporte |
|---|---|
| [1 — Gestion des patients](Historique/etape-1-patients/readme.md) | le socle — registre, passerelle, bibliothèque partagée — puis les fiches patients et l'interface web |
| [2 — Notes](Historique/etape-2-notes/readme.md) | les notes de consultation, sur base documentaire |
| [3 — Risque](Historique/etape-3-risque/readme.md) | l'évaluation du risque de diabète, croisée à partir des notes |
| [4 — Swagger](Historique/etape-4-swagger/readme.md) | la documentation OpenAPI des trois API |
| [5 — Projet complet](Historique/etape-5-complet/readme.md) | le même code, commenté et documenté |
| [6 — Kubernetes](Historique/etape-6-kubernetes/readme.md) | le même projet, déployé sur un cluster au lieu de Docker Compose |

Les étapes 1 à 4 livrent le code **brut, sans commentaires ni Javadoc**, afin de ne montrer
que la structure. L'**étape 5** est la version de référence : c'est elle qu'il faut ouvrir
pour découvrir le projet plutôt que sa construction. L'**étape 6** n'en change pas le code —
elle en change le déploiement.

## Démarrer une étape

Les commandes se lancent depuis le dossier de l'étape, jamais depuis la racine :

```bash
cd Historique/etape-5-complet
./prod-start.sh    # pile complète en conteneurs
./mvnw verify      # compile, teste et produit les rapports qualité
```

L'étape 6 fait exception : elle se déploie sur un cluster minikube, avec ses propres
scripts.

```bash
cd Historique/etape-6-kubernetes
./k8s-start.sh     # construit les images et déploie la pile sur le cluster
./k8s-stop.sh      # supprime la pile, conserve les données
```

Les étapes 1 à 5 utilisent les **mêmes ports** et les mêmes noms de conteneurs : n'en faire
tourner qu'une à la fois, sinon arrêter la précédente avec son `./prod-stop.sh`.

Le détail — prérequis, comptes, API exposées, règle métier — figure dans le readme de
chaque étape.
