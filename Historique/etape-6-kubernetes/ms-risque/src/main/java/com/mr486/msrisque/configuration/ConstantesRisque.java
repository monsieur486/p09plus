package com.mr486.msrisque.configuration;

import java.util.List;

/**
 * Invariants métier du calcul du risque de diabète.
 *
 * <p>Ces valeurs proviennent du cahier des charges et ne dépendent pas de
 * l'environnement d'exécution : elles n'ont donc pas leur place dans la configuration
 * externe ({@code application.yml} ou {@code .env}).</p>
 *
 * <p><b>Exemple :</b> {@code ConstantesRisque.AGE_PIVOT} vaut 30, l'âge à partir duquel
 * un patient relève des règles « 30 ans et plus ».</p>
 */
public final class ConstantesRisque {

    /**
     * Termes déclencheurs recherchés dans les notes des praticiens.
     *
     * <p>Ils sont écrits en minuscules car la recherche compare le contenu des notes
     * lui aussi passé en minuscules, afin d'être insensible à la casse.</p>
     */
    // creedengo déconseille les collections statiques, qui retiennent la mémoire et
    // grossissent sans contrôle. Celle-ci est immuable (List.of), figée à onze entrées par
    // le cahier des charges, et constitue précisément la constante de domaine que les
    // conventions du projet imposent d'extraire du code.
    @SuppressWarnings("creedengo-java:GCI76")
    public static final List<String> TERMES_DECLENCHEURS = List.of(
            "hémoglobine a1c",
            "microalbumine",
            "taille",
            "poids",
            "fumeur",
            "anormal",
            "cholestérol",
            "vertige",
            "rechute",
            "réaction",
            "anticorps");

    /** Code du genre masculin tel que stocké sur la fiche patient. */
    public static final String GENRE_MASCULIN = "M";

    /** Nom du microservice des patients, tel qu'enregistré auprès du registre de services. */
    public static final String SERVICE_PATIENTS = "ms-patients";

    /** Nom du microservice des notes, tel qu'enregistré auprès du registre de services. */
    public static final String SERVICE_NOTES = "ms-notes";

    /** Âge séparant les règles « moins de 30 ans » des règles « 30 ans et plus ». */
    public static final int AGE_PIVOT = 30;

    /** Nombre de déclencheurs à partir duquel un patient de 30 ans ou plus est Borderline. */
    public static final int SEUIL_BORDERLINE = 2;

    /** Nombre de déclencheurs à partir duquel un patient de 30 ans ou plus est In Danger. */
    public static final int SEUIL_IN_DANGER_TRENTE_ET_PLUS = 6;

    /** Nombre de déclencheurs à partir duquel un patient de 30 ans ou plus est Early onset. */
    public static final int SEUIL_EARLY_ONSET_TRENTE_ET_PLUS = 8;

    /** Nombre de déclencheurs à partir duquel un homme de moins de 30 ans est In Danger. */
    public static final int SEUIL_IN_DANGER_HOMME_JEUNE = 3;

    /** Nombre de déclencheurs à partir duquel un homme de moins de 30 ans est Early onset. */
    public static final int SEUIL_EARLY_ONSET_HOMME_JEUNE = 5;

    /** Nombre de déclencheurs à partir duquel une femme de moins de 30 ans est In Danger. */
    public static final int SEUIL_IN_DANGER_FEMME_JEUNE = 4;

    /** Nombre de déclencheurs à partir duquel une femme de moins de 30 ans est Early onset. */
    public static final int SEUIL_EARLY_ONSET_FEMME_JEUNE = 7;

    private ConstantesRisque() {
        // classe utilitaire : pas d'instanciation
    }
}
