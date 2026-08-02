package com.mr486.msrisque.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.PatientClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests de la récupération des fiches patients auprès du microservice dédié.
 *
 * <p>La décision vérifiée ici est la distinction entre deux échecs de nature différente :
 * une fiche absente, imputable à la demande, et un service injoignable, imputable à
 * l'infrastructure. Les traduire de la même façon masquerait une panne.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Récupération d'une fiche patient")
class PatientServiceTest {

    /** Identifiant du patient manipulé par les scénarios. */
    private static final Long PATIENT_ID = 42L;

    @Mock
    private PatientClient patientClient;

    private PatientService patientService;

    @BeforeEach
    void initialiseLeService() {
        patientService = new PatientService(patientClient);
    }

    @Test
    @DisplayName("une fiche absente devient une ressource introuvable")
    void ficheAbsente_leveResourceNotFound() {
        when(patientClient.findById(PATIENT_ID)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> patientService.getPatientById(PATIENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    @DisplayName("un service des patients injoignable devient une indisponibilité de service")
    void serviceInjoignable_leveServiceIndisponible() {
        when(patientClient.findById(PATIENT_ID))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        assertThatThrownBy(() -> patientService.getPatientById(PATIENT_ID))
                .isInstanceOf(ServiceIndisponibleException.class)
                .hasMessageContaining("ms-patients");
    }
}
