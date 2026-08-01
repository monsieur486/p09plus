package com.mr486.mswebclient.service;

import com.mr486.mswebclient.configuration.ConstantesWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Demande l'évaluation du risque de diabète d'un patient au microservice dédié.
 *
 * <p><b>Exemple :</b> {@code evalueLeRisque(4L)} retourne {@code "Early onset"} pour un
 * patient jeune cumulant au moins cinq termes déclencheurs.</p>
 */
@Service
@RequiredArgsConstructor
public class EvaluationApiService {

    private final ClientPasserelle clientPasserelle;

    /**
     * Retourne le niveau de risque de diabète d'un patient.
     *
     * <p><b>Exemple :</b> {@code evalueLeRisque(1L)} retourne {@code "None"} pour un patient
     * dont aucune note ne contient de terme déclencheur.</p>
     *
     * @param patientId identifiant du patient à évaluer
     * @return le libellé du niveau de risque
     */
    public String evalueLeRisque(Long patientId) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_EVALUATION + patientId,
                null,
                new ParameterizedTypeReference<String>() {},
                ConstantesWebclient.SERVICE_RISQUE);
    }
}
