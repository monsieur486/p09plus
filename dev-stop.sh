#!/usr/bin/env bash
# Arrête les dépendances de développement en conservant les données.
# Pour repartir d'une base vierge, utiliser explicitement : docker compose down -v
set -euo pipefail

docker compose down
echo "Bases arrêtées. Les volumes de données sont conservés."
