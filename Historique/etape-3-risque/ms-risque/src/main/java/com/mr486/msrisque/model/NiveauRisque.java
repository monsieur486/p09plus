package com.mr486.msrisque.model;

public enum NiveauRisque {
    NONE("None"),

    BORDERLINE("Borderline"),

    IN_DANGER("In Danger"),

    EARLY_ONSET("Early onset");

    private final String libelle;

    NiveauRisque(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
