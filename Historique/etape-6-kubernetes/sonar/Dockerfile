# Serveur SonarQube doté des règles creedengo (Green Code Initiative), pour l'analyse
# écologique du code en développement local.
#
# Le greffon n'est pas distribué par Maven Central : c'est un jar à déposer dans le dossier
# des extensions du serveur. Le construire dans l'image plutôt que le monter en volume rend
# l'installation reproductible et versionnée — rien à télécharger à la main, rien à installer
# depuis la place de marché de SonarQube.
#
# Les deux versions sont liées : creedengo 2.2.0 annonce la compatibilité de SonarQube 26.1
# à 26.6. Le tag ci-dessous est donc le plus récent que cette matrice couvre — 26.7 existe,
# mais sort du domaine testé par le greffon. Ne monter l'un sans vérifier l'autre.
#
# Le tag porte le numéro de construction : « 26.6.0-community » n'existe pas côté registre,
# seul « 26.6.0.123539-community » est publié.
FROM sonarqube:26.6.0.123539-community

# Version du greffon creedengo pour le langage Java.
ARG CREEDENGO_VERSION=2.2.0

# Le jar doit appartenir à l'utilisateur qui fait tourner le serveur, sans quoi le
# chargement des extensions échoue au démarrage.
ADD --chown=sonarqube:sonarqube --chmod=644 \
    https://github.com/green-code-initiative/creedengo-java/releases/download/${CREEDENGO_VERSION}/creedengo-java-plugin-${CREEDENGO_VERSION}.jar \
    /opt/sonarqube/extensions/plugins/
