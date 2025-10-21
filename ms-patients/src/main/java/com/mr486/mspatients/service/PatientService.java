package com.mr486.mspatients.service;

import com.mr486.mspatients.dto.PatientAddForm;
import com.mr486.mspatients.dto.PatientUpdateForm;
import com.mr486.mspatients.exeption.DuplicateException;
import com.mr486.mspatients.exeption.NotFoundException;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;

  public List<Patient> findAll() {
    return patientRepository.findAll();
  }

  public Patient findById(Long id) {
    return patientRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Aucun patient avec l'id: " + id));
  }

  public Patient savePatient(PatientAddForm patientAddForm) {
    Patient patient = new Patient();
    patient.setFirstName(patientAddForm.getFirstName());
    patient.setLastName(patientAddForm.getLastName());
    patient.setBirthDate(patientAddForm.getBirthDate());
    patient.setGender(patientAddForm.getGender());
    patient.setPostalAddress(patientAddForm.getPostalAddress());
    patient.setPhoneNumber(patientAddForm.getPhoneNumber());
    if (patientExistsByInfos(
            patientAddForm.getLastName(),
            patientAddForm.getFirstName(),
            patientAddForm.getBirthDate(),
            patientAddForm.getGender())) {
      throw new DuplicateException("Le patient existe déjà dans la base de données.");
    }
    return patientRepository.save(patient);
  }

  public Patient updatePatient(Long id, PatientUpdateForm patientUpdateForm) {
    Patient existing = patientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Aucun patient avec l'id: " + id));

    existing.setPostalAddress(patientUpdateForm.getPostalAddress());
    existing.setPhoneNumber(patientUpdateForm.getPhoneNumber());

    return patientRepository.save(existing);

  }

  private Boolean patientExistsByInfos(
          String lastName, String firstName, LocalDate birthDate, String gender) {
    return patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
            lastName, firstName, birthDate, gender);
  }
}
