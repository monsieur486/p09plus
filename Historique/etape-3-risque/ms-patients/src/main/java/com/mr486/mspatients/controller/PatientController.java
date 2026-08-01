package com.mr486.mspatients.controller;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.PageDto;
import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.dto.PatientForm;
import com.mr486.mspatients.mapper.PatientMapper;
import com.mr486.mspatients.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping(value = "/patients", produces = "application/json")
    public ResponseEntity<PageDto<PatientDto>> getPatients(
            @RequestParam(defaultValue = ConstantesApplication.PREMIERE_PAGE_TEXTE) int page,
            @RequestParam(defaultValue = ConstantesApplication.TAILLE_PAGE_DEFAUT_TEXTE) int size) {
        Pageable pagination = PageRequest.of(
                Math.max(page, 0), Math.min(size, ConstantesApplication.TAILLE_PAGE_MAXIMALE));
        return ResponseEntity.ok(patientMapper.versPageDto(patientService.findAll(pagination)));
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
