package com.mr486.msnotes.controller;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msnotes.mapper.NoteMapper;
import com.mr486.msnotes.service.NoteService;
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

@RestController
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final NoteMapper noteMapper;

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

    @PostMapping(value = "/patients/{patientId}/notes",
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<NoteDto> addNote(
            @PathVariable Long patientId, @Valid @RequestBody NoteDto noteDto) {
        NoteDto creee = noteMapper.versDto(noteService.save(patientId, noteDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(creee);
    }
}
