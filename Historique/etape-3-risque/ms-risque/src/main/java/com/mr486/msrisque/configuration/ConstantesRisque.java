package com.mr486.msrisque.configuration;

import java.util.List;

public final class ConstantesRisque {
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

    public static final String GENRE_MASCULIN = "M";

    public static final String SERVICE_PATIENTS = "ms-patients";

    public static final String SERVICE_NOTES = "ms-notes";

    public static final int AGE_PIVOT = 30;

    public static final int SEUIL_BORDERLINE = 2;

    public static final int SEUIL_IN_DANGER_TRENTE_ET_PLUS = 6;

    public static final int SEUIL_EARLY_ONSET_TRENTE_ET_PLUS = 8;

    public static final int SEUIL_IN_DANGER_HOMME_JEUNE = 3;

    public static final int SEUIL_EARLY_ONSET_HOMME_JEUNE = 5;

    public static final int SEUIL_IN_DANGER_FEMME_JEUNE = 4;

    public static final int SEUIL_EARLY_ONSET_FEMME_JEUNE = 7;

    private ConstantesRisque() {
    }
}
