package com.mr486.msrisque.controller;

import com.mr486.msrisque.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;

    @GetMapping(value = "/evaluation/{patientId}", produces = "text/plain")
    public ResponseEntity<String> evaluate(@PathVariable Long patientId) {
        return ResponseEntity.ok(evaluationService.evaluationDuRisque(patientId));
    }
}
