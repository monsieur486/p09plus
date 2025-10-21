package com.mr486.mspatients.controller;

import com.mr486.mspatients.dto.PatientForm;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

  private final PatientService patientService;

  @GetMapping(value = "", produces = "application/json")
  public ResponseEntity<List<Patient>> getPatients() {
    return ResponseEntity.ok(patientService.findAll());
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
    Patient patient = patientService.findById(id);
    return ResponseEntity.ok(patient);
  }

  @PostMapping(value = "", consumes = "application/json", produces = "application/json")
  public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientForm patientForm) {
    Patient savedPatient = patientService.savePatient(patientForm);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
  }

  @PutMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<Patient> update(@PathVariable Long id, @Valid @RequestBody PatientForm form) {
    Patient updated = patientService.updatePatient(id, form);
    return ResponseEntity.ok(updated);
  }
}
