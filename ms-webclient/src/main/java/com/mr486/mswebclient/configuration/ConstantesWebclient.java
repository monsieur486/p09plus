package com.mr486.mswebclient.configuration;

/**
 * Invariants de l'interface web : noms des microservices et chemins de leurs API.
 *
 * <p>Le premier segment de chaque chemin est le nom du microservice : c'est lui que la
 * passerelle utilise pour router l'appel, avant de le retirer. Renommer un service impose
 * donc de corriger ces valeurs.</p>
 *
 * <p><b>Exemple :</b> {@code CHEMIN_PATIENTS} vaut {@code "/ms-patients/patients"}, que la
 * passerelle traduit en {@code GET /patients} sur le service des patients.</p>
 */
public final class ConstantesWebclient {

    /** Nom du microservice des patients, affiché dans les messages d'indisponibilité. */
    public static final String SERVICE_PATIENTS = "ms-patients";

    /** Nom du microservice des notes, affiché dans les messages d'indisponibilité. */
    public static final String SERVICE_NOTES = "ms-notes";

    /** Nom du microservice d'évaluation, affiché dans les messages d'indisponibilité. */
    public static final String SERVICE_RISQUE = "ms-risque";

    /** Chemin des fiches patients derrière la passerelle. */
    public static final String CHEMIN_PATIENTS = "/ms-patients/patients";

    /** Chemin des notes d'un patient derrière la passerelle, à compléter par l'identifiant. */
    public static final String CHEMIN_NOTES = "/ms-notes/patients/";

    /** Chemin de l'évaluation du risque derrière la passerelle, à compléter par l'identifiant. */
    public static final String CHEMIN_EVALUATION = "/ms-risque/evaluation/";

    private ConstantesWebclient() {
        // classe utilitaire : pas d'instanciation
    }
}
