package com.mr486.mspatients.controller;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.PageDto;
import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.dto.PatientForm;
import com.mr486.mspatients.mapper.PatientMapper;
import com.mr486.mspatients.service.PatientService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Expose les opérations de consultation et de gestion des fiches patients.
 *
 * <p>Le contrôleur se limite à l'adaptation HTTP : il délègue la règle métier au service
 * et ne renvoie que des objets de transport, jamais l'entité persistée.</p>
 *
 * <p><b>Exemple :</b> {@code GET /patients/1} retourne la fiche du patient 1 ;
 * {@code POST /patients} crée une fiche et retourne un statut 201.</p>
 */
@RestController
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Gestion des patients API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    /**
     * Retourne une page de patients.
     *
     * <p>La taille demandée est ramenée à une borne haute : un client ne peut donc pas
     * contourner la pagination en réclamant une page démesurée.</p>
     *
     * <p><b>Exemple :</b> {@code GET /patients?page=0&size=10} retourne les dix premiers
     * patients et le décompte total permettant de construire la navigation.</p>
     *
     * @param page numéro de la page demandée, à partir de zéro
     * @param size nombre d'éléments par page
     * @return la page de patients, avec un statut 200
     */
    @Tag(name = "Récupère les patients par page")
    @GetMapping(value = "/patients", produces = "application/json")
    public ResponseEntity<PageDto<PatientDto>> getPatients(
            @RequestParam(defaultValue = ConstantesApplication.PREMIERE_PAGE_TEXTE) int page,
            @RequestParam(defaultValue = ConstantesApplication.TAILLE_PAGE_DEFAUT_TEXTE) int size) {
        Pageable pagination = PageRequest.of(
                Math.max(page, 0), Math.min(size, ConstantesApplication.TAILLE_PAGE_MAXIMALE));
        return ResponseEntity.ok(patientMapper.versPageDto(patientService.findAll(pagination)));
    }

    /**
     * Retourne un patient désigné par son identifiant.
     *
     * <p><b>Exemple :</b> {@code GET /patients/1} retourne la fiche du patient 1 ; un
     * identifiant inconnu retourne un statut 404.</p>
     *
     * @param id identifiant du patient recherché
     * @return la fiche demandée, avec un statut 200
     */
    @Tag(name = "Récupère un patient par son ID")
    @GetMapping(value = "/patients/{id}", produces = "application/json")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientMapper.versDto(patientService.findById(id)));
    }

    /**
     * Crée un patient à partir des données transmises.
     *
     * <p><b>Exemple :</b> {@code POST /patients} retourne un statut 201 et la fiche créée ;
     * un patient de même identité retourne un statut 409.</p>
     *
     * @param patientForm données du patient à créer
     * @return la fiche créée, avec un statut 201
     */
    @Tag(name = "Crée un nouveau patient")
    @PostMapping(value = "/patients", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientForm patientForm) {
        PatientDto cree = patientMapper.versDto(patientService.savePatient(patientForm));
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * Met à jour un patient existant.
     *
     * <p><b>Exemple :</b> {@code PUT /patients/1} retourne la fiche mise à jour ; un
     * identifiant inconnu retourne un statut 404 et une identité déjà prise un statut 409.</p>
     *
     * @param id          identifiant du patient à mettre à jour
     * @param patientForm nouvelles données du patient
     * @return la fiche mise à jour, avec un statut 200
     */
    @Tag(name = "Met à jour un patient existant")
    @PutMapping(value = "/patients/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatientDto> update(
            @PathVariable Long id, @Valid @RequestBody PatientForm patientForm) {
        return ResponseEntity.ok(patientMapper.versDto(patientService.updatePatient(id, patientForm)));
    }
}
