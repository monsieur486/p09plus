package com.mr486.mspatients.service;

import com.mr486.commun.dto.PatientForm;
import com.mr486.commun.exception.DuplicateException;
import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.repository.PatientRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun patient avec l'id: " + id));
    }

    public Patient savePatient(PatientForm patientForm) {
        verifieAbsenceDeDoublon(patientForm);

        Patient patient = new Patient();
        appliqueLeFormulaire(patient, patientForm);
        Patient enregistre = patientRepository.save(patient);

        log.info("patient {} créé", enregistre.getId());
        return enregistre;
    }

    public Patient updatePatient(Long id, PatientForm patientForm) {
        Patient existant = findById(id);

        if (identiteModifiee(existant, patientForm)) {
            verifieAbsenceDeDoublon(patientForm);
        }
        appliqueLeFormulaire(existant, patientForm);
        Patient miseAJour = patientRepository.save(existant);

        log.info("patient {} mis à jour", id);
        return miseAJour;
    }

    private void verifieAbsenceDeDoublon(PatientForm patientForm) {
        boolean existeDeja = patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
                patientForm.getLastName(),
                patientForm.getFirstName(),
                patientForm.getBirthDate(),
                patientForm.getGender());

        log.debug("contrôle d'unicité effectué, doublon détecté : {}", existeDeja);
        if (existeDeja) {
            throw new DuplicateException("Le patient existe déjà dans la base de données.");
        }
    }

    private boolean identiteModifiee(Patient existant, PatientForm patientForm) {
        return !Objects.equals(existant.getLastName(), patientForm.getLastName())
                || !Objects.equals(existant.getFirstName(), patientForm.getFirstName())
                || !Objects.equals(existant.getBirthDate(), patientForm.getBirthDate())
                || !Objects.equals(existant.getGender(), patientForm.getGender());
    }

    private void appliqueLeFormulaire(Patient patient, PatientForm patientForm) {
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setBirthDate(patientForm.getBirthDate());
        patient.setGender(patientForm.getGender());
        patient.setPostalAddress(patientForm.getPostalAddress());
        patient.setPhoneNumber(patientForm.getPhoneNumber());
    }
}
