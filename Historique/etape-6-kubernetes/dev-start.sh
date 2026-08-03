#!/usr/bin/env bash
# Démarre les dépendances du développement local : les deux bases de données et le serveur
# SonarQube. Les applications Java, elles, se lancent depuis l'IDE ou avec
# ./mvnw spring-boot:run, ce qui permet le rechargement à chaud et le débogage.
set -euo pipefail

# SonarQube embarque Elasticsearch, qui refuse de démarrer si le noyau limite trop le nombre
# de zones mémoire projetables. Le contrôle est fait ici plutôt que subi au démarrage : le
# message du conteneur, lui, se perd dans les journaux. Aucune élévation de privilège n'est
# tentée — la commande est affichée, c'est à l'utilisateur de la passer.
MAP_COUNT_MINIMAL=524288
map_count_actuel=$(sysctl -n vm.max_map_count 2>/dev/null || echo 0)
if (( map_count_actuel < MAP_COUNT_MINIMAL )); then
    echo "ATTENTION : vm.max_map_count vaut ${map_count_actuel}, SonarQube en exige ${MAP_COUNT_MINIMAL}."
    echo "            Le conteneur sonarqube refusera de démarrer. Pour corriger :"
    echo "               sudo sysctl -w vm.max_map_count=${MAP_COUNT_MINIMAL}"
    echo "            (à rendre permanent dans /etc/sysctl.d/ pour survivre au redémarrage)"
    echo
fi

docker compose up -d
echo "Bases démarrées. Lancez ensuite ms-eureka, puis les microservices."
echo "SonarQube démarre sur http://localhost:9001 — compter une minute avant qu'il réponde."
echo "Pour analyser le code : ./sonar-scan.sh"
