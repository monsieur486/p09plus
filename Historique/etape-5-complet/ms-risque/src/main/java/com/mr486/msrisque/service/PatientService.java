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

/**
 * Récupère les fiches patients auprès du microservice dédié.
 *
 * <p>Les échecs d'appel sont traduits en exceptions métier : une fiche absente devient une
 * {@link ResourceNotFoundException}, tandis qu'un service arrêté ou injoignable devient une
 * {@link ServiceIndisponibleException}. L'appelant n'a donc pas à connaître le mécanisme
 * de communication employé.</p>
 *
 * <p><b>Exemple :</b> {@code getPatientById(1L)} retourne la fiche du patient 1 ; si le
 * service des patients est arrêté, l'appel lève une {@link ServiceIndisponibleException}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientClient patientClient;

    /**
     * Retourne la fiche d'un patient désigné par son identifiant.
     *
     * <p><b>Exemple :</b> {@code getPatientById(42L)} lève une
     * {@link ResourceNotFoundException} si aucune fiche ne porte cet identifiant.</p>
     *
     * @param id identifiant du patient recherché
     * @return la fiche du patient
     * @throws ResourceNotFoundException    si aucun patient ne porte cet identifiant
     * @throws ServiceIndisponibleException si le microservice des patients ne répond pas
     */
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
