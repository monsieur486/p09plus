package com.mr486.mspatients.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mr486.commun.dto.PatientForm;
import com.mr486.commun.exception.DuplicateException;
import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.repository.PatientRepository;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests des règles métier de gestion des patients.
 *
 * <p>Seuls les comportements portant une décision sont vérifiés : contrôle d'unicité et
 * signalement d'une fiche absente. Les opérations qui se contentent de déléguer au
 * référentiel ne sont pas testées — elles ne vérifieraient que Spring Data.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Gestion des patients")
class PatientServiceTest {

    /** Identifiant du patient manipulé par les scénarios. */
    private static final Long PATIENT_ID = 1L;

    /** Date de naissance de référence des jeux d'essai. */
    private static final LocalDate NAISSANCE = LocalDate.of(1990, Month.MAY, 12);

    @Mock
    private PatientRepository patientRepository;

    private PatientService patientService;

    @BeforeEach
    void initialiseLeService() {
        patientService = new PatientService(patientRepository);
    }

    @Test
    @DisplayName("un identifiant inconnu lève ResourceNotFoundException")
    void findById_inconnu_leveResourceNotFound() {
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.findById(PATIENT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    @DisplayName("un patient déjà présent lève DuplicateException sans être enregistré")
    void savePatient_doublon_leveDuplicate() {
        when(patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
                anyString(), anyString(), any(LocalDate.class), anyString())).thenReturn(true);

        final PatientForm formulaire = unFormulaire();

        assertThatThrownBy(() -> patientService.savePatient(formulaire))
                .isInstanceOf(DuplicateException.class);

        verify(patientRepository, never()).save(ArgumentMatchers.<Patient>any());
    }

    @Test
    @DisplayName("la mise à jour d'un identifiant inconnu lève ResourceNotFoundException")
    void updatePatient_inconnu_leveResourceNotFound() {
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        final PatientForm formulaire = unFormulaire();

        assertThatThrownBy(() -> patientService.updatePatient(PATIENT_ID, formulaire))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("modifier un patient sans toucher à son identité ne déclenche pas de doublon")
    void updatePatient_identiteInchangee_neVerifiePasLesDoublons() {
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(unPatient()));
        when(patientRepository.save(any(Patient.class))).thenAnswer(appel -> appel.getArgument(0));

        PatientForm formulaire = unFormulaire();
        formulaire.setPostalAddress("12 rue Neuve");

        patientService.updatePatient(PATIENT_ID, formulaire);

        verify(patientRepository, never()).existsByLastNameAndFirstNameAndBirthDateAndGender(
                anyString(), anyString(), any(LocalDate.class), anyString());
    }

    @Test
    @DisplayName("renommer un patient vers une identité déjà prise lève DuplicateException")
    void updatePatient_versUnDoublon_leveDuplicate() {
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(unPatient()));
        when(patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
                "Martin", "Jean", NAISSANCE, "M")).thenReturn(true);

        PatientForm formulaire = unFormulaire();
        formulaire.setLastName("Martin");

        assertThatThrownBy(() -> patientService.updatePatient(PATIENT_ID, formulaire))
                .isInstanceOf(DuplicateException.class);

        verify(patientRepository, never()).save(ArgumentMatchers.<Patient>any());
    }

    // Construit l'entité patient de référence des scénarios.
    private Patient unPatient() {
        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        patient.setFirstName("Jean");
        patient.setLastName("Dupont");
        patient.setBirthDate(NAISSANCE);
        patient.setGender("M");
        patient.setPostalAddress("1 rue des Lilas");
        patient.setPhoneNumber("100-222-3333");
        return patient;
    }

    // Construit un formulaire reprenant l'identité du patient de référence.
    private PatientForm unFormulaire() {
        return PatientForm.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .birthDate(NAISSANCE)
                .gender("M")
                .postalAddress("1 rue des Lilas")
                .phoneNumber("100-222-3333")
                .build();
    }
}
