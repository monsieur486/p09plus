#!/usr/bin/env bash
# Construit et démarre la pile complète : bases, registre, passerelle, microservices et IHM.
# Les trois microservices métier sont répliqués pour se rapprocher d'un environnement réel ;
# leur nombre d'instances est réglé par les variables REPLICA_MS_* du .env, que Docker
# Compose lit lui-même. Le script ne les relaie pas : un shell ne lit pas le .env, et les
# passer en --scale ici ferait diverger la commande du fichier de configuration.
set -euo pipefail

docker compose --profile fullstack up -d --build

echo "Pile démarrée : IHM sur http://localhost:8080, Swagger sur http://localhost:9000/swagger-ui/index.html"
