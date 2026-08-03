#!/usr/bin/env bash
# Analyse le code de l'étape avec SonarQube et les règles creedengo (Green Code Initiative).
#
# Le script est relançable sans effet de bord : chaque préalable est vérifié avant d'être
# posé. Au premier passage il met le serveur en état de servir (mot de passe, jeton, profil
# de règles) ; aux suivants il ne fait plus que l'analyse.
#
# Prérequis : le serveur tourne (./dev-start.sh) et Docker est disponible pour les tests
# d'intégration lancés par `verify`.
set -euo pipefail

URL_SONAR="${SONAR_URL:-http://localhost:9001}"
# Mot de passe administrateur du serveur local. Valeur de développement, sans enjeu : le
# serveur n'est joignable que depuis le poste et ne contient que des rapports d'analyse.
# SonarQube impose sa propre politique — majuscule, minuscule, chiffre et longueur — et
# refuse en 400 tout mot de passe qui n'y répond pas.
MDP_ADMIN="${SONAR_ADMIN_PASSWORD:-P09plus-Sonar1}"
FICHIER_JETON=".sonar-token"
NOM_JETON="p09plus-scan"
NOM_PROFIL="creedengo way"
DELAI_DEMARRAGE=180

command -v curl >/dev/null || { echo "ERREUR : curl est introuvable." >&2; exit 1; }
command -v jq >/dev/null || { echo "ERREUR : jq est introuvable (apt install jq)." >&2; exit 1; }

# Appel d'API authentifié par le compte administrateur.
api_admin() {
    curl -sS -u "admin:${MDP_ADMIN}" "$@"
}

echo "== Attente de SonarQube (${URL_SONAR})"
for ((seconde = 0; seconde < DELAI_DEMARRAGE; seconde++)); do
    etat=$(curl -sS "${URL_SONAR}/api/system/status" 2>/dev/null | jq -r '.status // empty')
    [[ "$etat" == "UP" ]] && break
    sleep 1
done
if [[ "${etat:-}" != "UP" ]]; then
    echo "ERREUR : le serveur n'a pas répondu 'UP' en ${DELAI_DEMARRAGE} s (état : ${etat:-aucun})." >&2
    echo "         Vérifier ses journaux : docker compose logs sonarqube" >&2
    exit 1
fi

# Le serveur naît avec le couple admin/admin, qu'il faut remplacer avant tout autre appel.
# Le code de retour est seul concluant : le corps est vide en cas de succès, et porte un
# libellé différent selon la raison du refus.
echo "== Compte administrateur"
code_http=$(curl -sS -o /dev/null -w '%{http_code}' -u "admin:admin" -X POST \
    --data-urlencode "login=admin" \
    --data-urlencode "previousPassword=admin" \
    --data-urlencode "password=${MDP_ADMIN}" \
    "${URL_SONAR}/api/users/change_password")
case "$code_http" in
    2*)  echo "   mot de passe initialisé" ;;
    # admin/admin n'est plus accepté : le mot de passe a été posé à un lancement précédent.
    401) echo "   déjà initialisé" ;;
    *)   echo "ERREUR : changement du mot de passe refusé (HTTP ${code_http})." >&2
         curl -sS -u "admin:admin" -X POST \
             --data-urlencode "login=admin" \
             --data-urlencode "previousPassword=admin" \
             --data-urlencode "password=${MDP_ADMIN}" \
             "${URL_SONAR}/api/users/change_password" >&2
         echo >&2
         exit 1 ;;
esac

# Un jeton ne peut pas être relu après sa création : on conserve le nôtre. S'il a disparu
# côté serveur (volume purgé), le fichier local ne vaut plus rien et il faut le refaire.
echo "== Jeton d'analyse"
jeton=""
if [[ -f "$FICHIER_JETON" ]]; then
    jeton=$(<"$FICHIER_JETON")
    if ! curl -sS -u "${jeton}:" "${URL_SONAR}/api/authentication/validate" | jq -e '.valid' >/dev/null; then
        echo "   le jeton conservé n'est plus valide, régénération"
        jeton=""
    fi
fi
if [[ -z "$jeton" ]]; then
    # Le nom peut être déjà pris côté serveur alors que le fichier local a disparu :
    # révoquer d'abord rend l'opération rejouable.
    api_admin -X POST --data-urlencode "name=${NOM_JETON}" \
        "${URL_SONAR}/api/user_tokens/revoke" >/dev/null
    jeton=$(api_admin -X POST --data-urlencode "name=${NOM_JETON}" \
        "${URL_SONAR}/api/user_tokens/generate" | jq -r '.token')
    [[ -n "$jeton" && "$jeton" != "null" ]] || { echo "ERREUR : jeton non généré." >&2; exit 1; }
    umask 077
    printf '%s' "$jeton" > "$FICHIER_JETON"
    echo "   jeton généré et conservé dans ${FICHIER_JETON}"
else
    echo "   jeton existant réutilisé"
fi

# Les règles creedengo ne figurent dans aucun profil livré : sans ce profil, l'analyse
# tourne mais ne remonte pas une seule remarque d'écoconception.
echo "== Profil « ${NOM_PROFIL} »"
cle_profil=$(api_admin -G --data-urlencode "language=java" --data-urlencode "qualityProfile=${NOM_PROFIL}" \
    "${URL_SONAR}/api/qualityprofiles/search" | jq -r '.profiles[0].key // empty')

if [[ -z "$cle_profil" ]]; then
    # Le dépôt de règles est cherché plutôt que codé en dur : sa clé a déjà changé une fois
    # avec le passage d'ecoCode à creedengo.
    depot_creedengo=$(api_admin -G --data-urlencode "language=java" \
        "${URL_SONAR}/api/rules/repositories" \
        | jq -r '.repositories[].key | select(test("creedengo|ecocode"))' | head -1)
    [[ -n "$depot_creedengo" ]] || {
        echo "ERREUR : aucun dépôt de règles creedengo. Le greffon est-il chargé ?" >&2
        echo "         Vérifier : ${URL_SONAR}/admin/marketplace" >&2
        exit 1
    }

    cle_profil=$(api_admin -X POST --data-urlencode "language=java" --data-urlencode "name=${NOM_PROFIL}" \
        "${URL_SONAR}/api/qualityprofiles/create" | jq -r '.profile.key')

    # Hérité de « Sonar way » : les règles creedengo s'ajoutent aux contrôles habituels au
    # lieu de s'y substituer.
    api_admin -X POST --data-urlencode "language=java" \
        --data-urlencode "qualityProfile=${NOM_PROFIL}" \
        --data-urlencode "parentQualityProfile=Sonar way" \
        "${URL_SONAR}/api/qualityprofiles/change_parent" >/dev/null

    api_admin -X POST --data-urlencode "targetKey=${cle_profil}" \
        --data-urlencode "repositories=${depot_creedengo}" \
        "${URL_SONAR}/api/qualityprofiles/activate_rules" >/dev/null

    # Profil par défaut du langage : l'analyse Maven n'a alors rien à déclarer.
    api_admin -X POST --data-urlencode "language=java" \
        --data-urlencode "qualityProfile=${NOM_PROFIL}" \
        "${URL_SONAR}/api/qualityprofiles/set_default" >/dev/null

    nombre_regles=$(api_admin -G --data-urlencode "language=java" \
        --data-urlencode "qualityProfile=${NOM_PROFIL}" \
        "${URL_SONAR}/api/qualityprofiles/search" | jq -r '.profiles[0].activeRuleCount')
    echo "   profil créé depuis « Sonar way » + dépôt ${depot_creedengo} — ${nombre_regles} règles actives"
else
    echo "   profil déjà en place"
fi

echo "== Analyse du code"
# `verify` avant `sonar:sonar` : l'analyse lit les classes compilées et le rapport de
# couverture JaCoCo, qui n'existent qu'une fois les tests passés.
#
# Le jeton passe par l'environnement, que l'analyseur lit nativement, et non par un
# -Dsonar.token : la ligne de commande d'un processus est lisible par tout compte du poste
# (`ps aux`), l'environnement ne l'est pas.
SONAR_TOKEN="${jeton}" SONAR_HOST_URL="${URL_SONAR}" ./mvnw verify sonar:sonar

echo
echo "Analyse terminée."
echo "  Rapport : ${URL_SONAR}/dashboard?id=p09plus-etape6"
echo "  Remarques d'écoconception : filtrer par le mot-clé « creedengo » dans les règles."
