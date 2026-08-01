package com.mr486.mspatients.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Gestion des patients")
class PatientServiceTest {

    private static final Long PATIENT_ID = 1L;

    private static final LocalDate NAISSANCE = LocalDate.of(1990, 5, 12);

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

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

        assertThatThrownBy(() -> patientService.savePatient(unFormulaire()))
                .isInstanceOf(DuplicateException.class);

        verify(patientRepository, never()).save(ArgumentMatchers.<Patient>any());
    }

    @Test
    @DisplayName("la mise à jour d'un identifiant inconnu lève ResourceNotFoundException")
    void updatePatient_inconnu_leveResourceNotFound() {
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatient(PATIENT_ID, unFormulaire()))
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
