package com.mr486.mspatients.service;

import com.mr486.mspatients.dto.PatientForm;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;

  public List<Patient> findAll() {
    return patientRepository.findAll();
  }

  public Optional<Patient> findById(Long id) {
    return patientRepository.findById(id);
  }

  public Patient savePatient(PatientForm patientForm) {
    Patient patient = patientForm.toEntity();
    return patientRepository.save(patient);
  }

  public Optional<Patient> updatePatient(Long id, PatientForm patientForm) {
    return patientRepository.findById(id)
            .map(existingPatient -> {
              Patient updatedPatient = patientForm.toEntity();
              updatedPatient.setId(existingPatient.getId());
              return patientRepository.save(updatedPatient);
            });
  }
}
