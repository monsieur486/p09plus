package com.mr486.msrisque.controller;

import com.mr486.msrisque.service.EvaluationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose l'évaluation du risque de diabète d'un patient.
 *
 * <p>Le contrôleur se limite à l'adaptation HTTP : le croisement des notes, de l'âge et du
 * genre est réalisé par le service métier.</p>
 *
 * <p><b>Exemple :</b> {@code GET /evaluation/1} retourne {@code None} ; si le service des
 * patients ou celui des notes est arrêté, la réponse est un statut 503.</p>
 */
@RestController
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Gestion du risque de diabète d'un patient API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Retourne le niveau de risque de diabète d'un patient.
     *
     * <p><b>Exemple :</b> {@code GET /evaluation/4} retourne {@code Early onset} pour un
     * patient jeune cumulant au moins cinq termes déclencheurs ; un patient inconnu
     * retourne un statut 404.</p>
     *
     * @param patientId identifiant du patient à évaluer
     * @return le libellé du niveau de risque, avec un statut 200
     */
    @Tag(name = "Évalue le risque de diabète d'un patient par son ID")
    @GetMapping(value = "/evaluation/{patientId}", produces = "text/plain")
    public ResponseEntity<String> evaluate(@PathVariable Long patientId) {
        return ResponseEntity.ok(evaluationService.evaluationDuRisque(patientId));
    }
}
