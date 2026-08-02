#!/usr/bin/env bash
# Construit et démarre la pile complète : bases, registre, passerelle, microservices et IHM.
# Le nombre d'instances des microservices métier est réglé par les variables REPLICA_MS_*
# du .env, que Docker Compose lit lui-même.
set -euo pipefail

docker compose --profile fullstack up -d --build

echo "Pile démarrée : IHM sur http://localhost:8080, Swagger sur http://localhost:9000/swagger-ui/index.html"
