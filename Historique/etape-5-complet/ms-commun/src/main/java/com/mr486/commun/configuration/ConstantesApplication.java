package com.mr486.commun.configuration;

/**
 * Constantes générales, transverses à tous les microservices.
 *
 * <p>Ces valeurs sont des invariants applicatifs, pas des réglages d'environnement : elles
 * n'ont donc pas leur place dans {@code application.yml} ni dans {@code .env}.</p>
 *
 * <p><b>Exemple :</b> {@code ConstantesApplication.TAILLE_PAGE_DEFAUT} vaut 10, le nombre
 * d'éléments affichés par page lorsqu'aucune taille n'est demandée.</p>
 */
public final class ConstantesApplication {

    /** Nombre d'éléments par page par défaut : compromis entre lisibilité et nombre d'appels. */
    public static final int TAILLE_PAGE_DEFAUT = 10;

    /** Valeur de {@code TAILLE_PAGE_DEFAUT} sous forme de chaîne, pour les valeurs par défaut d'annotations. */
    public static final String TAILLE_PAGE_DEFAUT_TEXTE = "10";

    /** Numéro de la première page, la numérotation commençant à zéro. */
    public static final String PREMIERE_PAGE_TEXTE = "0";

    /** Borne haute de la taille de page : au-delà, la demande est ramenée à cette valeur. */
    public static final int TAILLE_PAGE_MAXIMALE = 100;

    private ConstantesApplication() {
        // classe utilitaire : pas d'instanciation
    }
}
