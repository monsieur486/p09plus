package com.mr486.msrisque.service;

import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.PatientClient;
import com.mr486.msrisque.configuration.ConstantesRisque;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientClient patientClient;

    public PatientDto getPatientById(Long id) {
        try {
            return patientClient.findById(id);
        } catch (FeignException.NotFound ex) {
            log.warn("patient {} introuvable auprès du service des patients", id);
            throw new ResourceNotFoundException("Aucun patient avec l'id: " + id);
        } catch (FeignException ex) {
            throw new ServiceIndisponibleException(ConstantesRisque.SERVICE_PATIENTS, ex);
        }
    }
}
