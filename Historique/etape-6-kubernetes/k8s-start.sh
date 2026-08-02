#!/usr/bin/env bash
# Construit les images et déploie la pile complète sur minikube.
#
# Les images sont construites dans le démon Docker de minikube plutôt que dans celui du
# poste : le cluster les trouve alors localement, sans registre à installer ni à alimenter.
# C'est ce qui permet aux manifestes de déclarer imagePullPolicy: Never.
set -euo pipefail

MODULES=(ms-eureka ms-gateway ms-patients ms-notes ms-risque ms-webclient)
HOTE_IHM="p09plus.local"
# Sans bornes, minikube voit toute la machine et finit par l'assécher. Ces valeurs laissent
# de quoi travailler à côté du cluster ; mesuré, la pile y tient sans un échec.
MEMOIRE_CLUSTER="8g"
CPUS_CLUSTER=8

echo "== Vérification des prérequis"
for outil in minikube kubectl docker; do
    command -v "$outil" >/dev/null || { echo "ERREUR : $outil est introuvable." >&2; exit 1; }
done

if ! minikube status >/dev/null 2>&1; then
    echo "Le cluster n'est pas démarré, lancement de minikube..."
    # La mémoire ne se règle qu'à la création : un cluster déjà bâti garde la sienne, et
    # seul un `minikube delete` permet d'en changer.
    minikube start --memory="$MEMOIRE_CLUSTER" --cpus="$CPUS_CLUSTER"
fi

# Le --cpus ci-dessus n'est pas toujours répercuté sur le conteneur ; la limite est donc
# posée directement, à chaque démarrage. L'opération est sans effet si elle est déjà en
# place, et s'applique à chaud. À faire avant de basculer sur le démon du cluster : c'est
# celui du poste qui héberge le conteneur minikube.
echo "== Bornage du cluster (${MEMOIRE_CLUSTER}, ${CPUS_CLUSTER} cœurs)"
docker update --cpus="$CPUS_CLUSTER" minikube >/dev/null

# L'Ingress n'est routé que si le contrôleur nginx tourne dans le cluster.
echo "== Activation des addons"
minikube addons enable ingress
# Confort de mise au point : le tableau de bord (`minikube dashboard`) et les métriques que
# `kubectl top` réclame. La pile n'en dépend pas ; ils coûtent moins de 30 Mio à eux trois.
minikube addons enable metrics-server
minikube addons enable dashboard

echo "== Construction des images dans le démon de minikube"
# Le shell hérite des variables qui font pointer docker vers le démon du cluster : les
# images construites ensuite y sont directement disponibles.
eval "$(minikube docker-env)"
for module in "${MODULES[@]}"; do
    echo "   -> p09plus/${module}:local"
    # Contexte à la racine de l'étape : chaque module hérite du pom parent et dépend de
    # ms-commun, le construire depuis son seul dossier échouerait.
    docker build -q -t "p09plus/${module}:local" -f "${module}/Dockerfile" . >/dev/null
done

# Relevé avant l'application des manifestes : les déploiements déjà en place tournent sur
# les images d'avant la reconstruction, et rien ne les en avertira. Le tag ne changeant
# jamais, `kubectl apply` ne voit aucune différence et laisse les pods en l'état.
DEPLOIEMENTS_EXISTANTS=$(kubectl get deployment -o name 2>/dev/null || true)

# Un Job terminé n'est pas rejoué par `kubectl apply` : sans cette suppression, un
# changelog Liquibase ajouté depuis le dernier démarrage ne serait jamais appliqué.
# Rejouer le Job est sans risque, Liquibase ne reprenant que les changesets inconnus.
echo "== Réarmement du Job de migration"
kubectl delete job/ms-patients-migration --ignore-not-found --wait=true

echo "== Application des manifestes"
kubectl apply -f k8s/

if [[ -n "$DEPLOIEMENTS_EXISTANTS" ]]; then
    echo "== Redémarrage des déploiements déjà en place"
    # Seuls ceux qui préexistaient : les autres viennent de naître avec la bonne image.
    for deploiement in $DEPLOIEMENTS_EXISTANTS; do
        kubectl rollout restart "$deploiement" >/dev/null
    done
fi

echo "== Attente des bases de données"
kubectl rollout status statefulset/patients-db --timeout=300s
kubectl rollout status statefulset/notes-db --timeout=300s

echo "== Attente de la migration Liquibase"
# Les instances de ms-patients patientent d'elles-mêmes par un initContainer ; on attend
# ici pour signaler l'échec de la migration tout de suite plutôt qu'au bout d'un délai.
kubectl wait --for=condition=complete job/ms-patients-migration --timeout=300s

echo "== Attente des microservices"
for module in "${MODULES[@]}"; do
    kubectl rollout status "deployment/$module" --timeout=300s
done

IP_CLUSTER=$(minikube ip)
echo
echo "Pile déployée."
echo "  Interface web : http://${HOTE_IHM}/"
echo
echo "  Si la page ne répond pas, vérifier que ${HOTE_IHM} pointe vers le cluster :"
echo "     echo \"${IP_CLUSTER} ${HOTE_IHM}\" | sudo tee -a /etc/hosts"
echo
echo "  Les API et Swagger ne sont pas exposés hors du cluster ; pour y accéder :"
echo "     kubectl port-forward service/ms-gateway 9000:9000"
echo "     puis http://localhost:9000/swagger-ui/index.html"
echo
echo "  Pour suivre la pile :"
echo "     kubectl get pods -w        état des pods, en direct"
echo "     kubectl top pods           consommation par pod"
echo "     minikube dashboard         tableau de bord, dans le navigateur"
