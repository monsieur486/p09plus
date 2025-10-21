package com.mr486.msnotes.controller;

import com.mr486.msnotes.dto.NoteDto;
import com.mr486.msnotes.model.Note;
import com.mr486.msnotes.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class NoteController {

  private final NoteService noteService;

  @GetMapping(value = "/{patientId}/notes", produces = "application/json")
  public List<Note> getNotes(@PathVariable Long patientId) {
    return noteService.findByPatientId(patientId);
  }

  @PostMapping(value = "/{patientId}/notes", consumes = "application/json")
  public Note addNote(@PathVariable Long patientId, @Valid @RequestBody NoteDto noteDto) {
    return noteService.save(patientId, noteDto);
  }

}
