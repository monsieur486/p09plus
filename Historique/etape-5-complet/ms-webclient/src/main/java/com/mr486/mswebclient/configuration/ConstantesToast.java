package com.mr486.mswebclient.configuration;

/**
 * Clés et niveaux des messages éphémères affichés après une action.
 *
 * <p>Un message est déposé avant une redirection, puis consommé par la page d'arrivée qui
 * l'affiche sous forme de bandeau. Le niveau détermine sa couleur : vert pour une
 * information, orange pour un avertissement, rouge pour une erreur.</p>
 *
 * <p><b>Exemple :</b> après la création d'un patient, un message de niveau
 * {@code INFO} annonce « Patient créé. » sur le tableau de bord.</p>
 */
public final class ConstantesToast {

    /** Clé du modèle portant le texte du message. */
    public static final String CLE_MESSAGE = "toastMessage";

    /** Clé du modèle portant le niveau du message. */
    public static final String CLE_NIVEAU = "toastNiveau";

    /** Niveau information : opération réussie, affiché en vert. */
    public static final String NIVEAU_INFO = "info";

    /** Niveau avertissement : opération aboutie mais à signaler, affiché en orange. */
    public static final String NIVEAU_AVERTISSEMENT = "avertissement";

    /** Niveau erreur : opération échouée, affiché en rouge. */
    public static final String NIVEAU_ERREUR = "erreur";

    private ConstantesToast() {
        // classe utilitaire : pas d'instanciation
    }
}
