#!/usr/bin/env bash
# Démarre uniquement les dépendances (bases de données) pour le développement local.
# Les applications Java, elles, se lancent depuis l'IDE ou avec ./mvnw spring-boot:run,
# ce qui permet le rechargement à chaud et le débogage.
set -euo pipefail

docker compose up -d
echo "Bases démarrées. Lancez ensuite ms-eureka, puis les microservices."
