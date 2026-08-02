#!/usr/bin/env bash
# Supprime la pile du cluster. Les données des bases sont conservées.
#
# Usage :
#   ./k8s-stop.sh            arrête la pile, conserve les données
#   ./k8s-stop.sh --purge    arrête la pile et supprime les volumes des bases
#
# Le cluster minikube lui-même reste démarré : `minikube stop` l'arrête, `minikube delete`
# le supprime.
set -euo pipefail

echo "== Suppression des objets de la pile"
# --ignore-not-found : le script reste utilisable sur une pile déjà arrêtée.
kubectl delete -f k8s/ --ignore-not-found

if [[ "${1:-}" == "--purge" ]]; then
    echo "== Suppression des volumes des bases"
    # Les réclamations de volume naissent du gabarit des StatefulSets et leur survivent :
    # sans cette suppression explicite, les données seraient retrouvées au prochain
    # démarrage — c'est justement le comportement voulu par défaut.
    kubectl delete pvc -l app.kubernetes.io/name=patients-db --ignore-not-found
    kubectl delete pvc -l app.kubernetes.io/name=notes-db --ignore-not-found
    echo "Pile arrêtée, données supprimées."
else
    echo "Pile arrêtée. Les données des bases sont conservées (--purge pour les effacer)."
fi
