package com.mr486.mspatients.controller;

import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.dto.PatientForm;
import com.mr486.mspatients.mapper.PatientMapper;
import com.mr486.mspatients.service.PatientService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping(value = "/patients", produces = "application/json")
    public ResponseEntity<List<PatientDto>> getPatients() {
        return ResponseEntity.ok(patientMapper.versListeDto(patientService.findAll()));
    }

    @GetMapping(value = "/patients/{id}", produces = "application/json")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientMapper.versDto(patientService.findById(id)));
    }

    @PostMapping(value = "/patients", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientForm patientForm) {
        PatientDto cree = patientMapper.versDto(patientService.savePatient(patientForm));
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @PutMapping(value = "/patients/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatientDto> update(
            @PathVariable Long id, @Valid @RequestBody PatientForm patientForm) {
        return ResponseEntity.ok(patientMapper.versDto(patientService.updatePatient(id, patientForm)));
    }
}
