package com.mr486.mswebclient.service;

import com.mr486.mswebclient.configuration.ConstantesWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationApiService {
    private final ClientPasserelle clientPasserelle;

    public String evalueLeRisque(Long patientId) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_EVALUATION + patientId,
                null,
                new ParameterizedTypeReference<String>() {},
                ConstantesWebclient.SERVICE_RISQUE);
    }
}
