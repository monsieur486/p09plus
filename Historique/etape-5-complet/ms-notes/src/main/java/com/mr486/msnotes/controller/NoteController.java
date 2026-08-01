package com.mr486.msnotes.controller;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msnotes.mapper.NoteMapper;
import com.mr486.msnotes.service.NoteService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose la consultation et l'ajout des notes rattachées à un patient.
 *
 * <p>Le contrôleur se limite à l'adaptation HTTP : il délègue au service et ne renvoie
 * que des objets de transport, jamais le document persisté.</p>
 *
 * <p><b>Exemple :</b> {@code GET /patients/2/notes} retourne les notes du patient 2 ;
 * {@code POST /patients/2/notes} en ajoute une et retourne un statut 201.</p>
 */
@RestController
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Gestion des notes d'un patient API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    /**
     * Retourne une page de notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code GET /patients/2/notes?page=0&size=10} retourne les dix
     * dernières notes du patient 2, et une page vide si aucune note n'a été saisie.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param page      numéro de la page demandée, à partir de zéro
     * @param size      nombre d'éléments par page
     * @return la page de notes du patient, avec un statut 200
     */
    @Tag(name = "Récupère les notes d'un patient par page")
    @GetMapping(value = "/patients/{patientId}/notes", produces = "application/json")
    public ResponseEntity<PageDto<NoteDto>> getNotes(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = ConstantesApplication.PREMIERE_PAGE_TEXTE) int page,
            @RequestParam(defaultValue = ConstantesApplication.TAILLE_PAGE_DEFAUT_TEXTE) int size) {
        Pageable pagination = PageRequest.of(
                Math.max(page, 0), Math.min(size, ConstantesApplication.TAILLE_PAGE_MAXIMALE));
        return ResponseEntity.ok(
                noteMapper.versPageDto(noteService.findByPatientId(patientId, pagination)));
    }

    /**
     * Ajoute une note à un patient.
     *
     * <p><b>Exemple :</b> {@code POST /patients/2/notes} retourne un statut 201 et la note
     * enregistrée ; un contenu vide retourne un statut 400.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param noteDto   contenu de la note à ajouter
     * @return la note enregistrée, avec un statut 201
     */
    @Tag(name = "Ajoute une note à un patient par son ID")
    @PostMapping(value = "/patients/{patientId}/notes",
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<NoteDto> addNote(
            @PathVariable Long patientId, @Valid @RequestBody NoteDto noteDto) {
        NoteDto creee = noteMapper.versDto(noteService.save(patientId, noteDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(creee);
    }
}
