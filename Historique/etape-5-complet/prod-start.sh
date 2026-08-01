#!/usr/bin/env bash
# Construit et démarre la pile complète : bases, registre, passerelle, microservices et IHM.
# Les trois microservices métier sont répliqués pour se rapprocher d'un environnement réel.
set -euo pipefail

REPLICA_MS_PATIENTS="${REPLICA_MS_PATIENTS:-3}"
REPLICA_MS_NOTES="${REPLICA_MS_NOTES:-3}"
REPLICA_MS_RISQUE="${REPLICA_MS_RISQUE:-3}"

docker compose --profile fullstack up -d --build \
  --scale "ms-patients=${REPLICA_MS_PATIENTS}" \
  --scale "ms-notes=${REPLICA_MS_NOTES}" \
  --scale "ms-risque=${REPLICA_MS_RISQUE}"

echo "Pile démarrée : IHM sur http://localhost:8080, Swagger sur http://localhost:9000/swagger-ui/index.html"
