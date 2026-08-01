package com.mr486.msrisque.model;

/**
 * Niveaux de risque de diabète pouvant être attribués à un patient.
 *
 * <p>Le libellé porté par chaque valeur est celui attendu par les consommateurs de
 * l'API : il ne doit pas être modifié sans adapter les clients.</p>
 *
 * <p><b>Exemple :</b> {@code NiveauRisque.IN_DANGER.getLibelle()} retourne
 * {@code "In Danger"}.</p>
 */
public enum NiveauRisque {

    /** Aucun terme déclencheur relevé dans les notes du patient. */
    NONE("None"),

    /** Risque limité : patient de 30 ans ou plus présentant 2 à 5 déclencheurs. */
    BORDERLINE("Borderline"),

    /** Risque avéré nécessitant un suivi. */
    IN_DANGER("In Danger"),

    /** Risque le plus élevé : apparition précoce. */
    EARLY_ONSET("Early onset");

    /** Libellé exposé par l'API pour ce niveau de risque. */
    private final String libelle;

    NiveauRisque(String libelle) {
        this.libelle = libelle;
    }

    /**
     * Retourne le libellé exposé par l'API pour ce niveau de risque.
     *
     * <p><b>Exemple :</b> {@code NiveauRisque.EARLY_ONSET.getLibelle()} retourne
     * {@code "Early onset"}.</p>
     *
     * @return le libellé lisible du niveau de risque
     */
    public String getLibelle() {
        return libelle;
    }
}
