package com.mr486.mspatients.service;

import com.mr486.mspatients.dto.PatientForm;
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

  public Patient savePatient(PatientForm patientForm) {
    Patient patient = patientForm.toEntity();
    if (patientExistsByInfos(
            patientForm.getLastName(),
            patientForm.getFirstName(),
            patientForm.getBirthDate(),
            patientForm.getGender())) {
      throw new DuplicateException("Le patient existe déjà dans la base de données.");
    }
    return patientRepository.save(patient);
  }

  public Patient updatePatient(Long id, PatientForm patientForm) {
    Patient existing = patientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Aucun patient avec l'id: " + id));

    existing.setFirstName(patientForm.getFirstName());
    existing.setLastName(patientForm.getLastName());
    existing.setBirthDate(patientForm.getBirthDate());
    existing.setGender(patientForm.getGender());
    existing.setPostalAddress(patientForm.getPostalAddress());
    existing.setPhoneNumber(patientForm.getPhoneNumber());

    return patientRepository.save(existing);


  }

  private Boolean patientExistsByInfos(
          String lastName, String firstName, LocalDate birthDate, String gender) {
    return patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
            lastName, firstName, birthDate, gender);
  }
}
