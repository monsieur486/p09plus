#!/usr/bin/env bash
# Arrête les dépendances de développement (bases et SonarQube) en conservant les données.
# Pour repartir d'une base vierge, utiliser explicitement : docker compose down -v
# Ce dernier efface aussi l'historique d'analyse de SonarQube et son jeton : le fichier
# .sonar-token devient alors caduc et sonar-scan.sh en régénère un.
set -euo pipefail

docker compose down
echo "Bases et SonarQube arrêtés. Les volumes de données sont conservés."
