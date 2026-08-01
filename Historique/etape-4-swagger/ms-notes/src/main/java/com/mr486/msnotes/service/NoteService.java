package com.mr486.msnotes.service;

import com.mr486.commun.dto.NoteDto;
import com.mr486.msnotes.model.Note;
import com.mr486.msnotes.repository.NoteRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    public Page<Note> findByPatientId(Long patientId, Pageable pagination) {
        Page<Note> notes = noteRepository.findByPatientIdOrderByCreatedDateDesc(patientId, pagination);
        if (notes.isEmpty()) {
            log.warn("aucune note enregistrée pour le patient {}", patientId);
        }
        return notes;
    }

    public Note save(Long patientId, NoteDto noteDto) {
        Note enregistree = noteRepository.save(construitNote(patientId, noteDto));
        log.info("note créée pour le patient {}", patientId);
        return enregistree;
    }

    private Note construitNote(Long patientId, NoteDto noteDto) {
        log.debug("construction d'une note pour le patient {}", patientId);
        return Note.builder()
                .patientId(patientId)
                .content(noteDto.getContent())
                .createdDate(Instant.now())
                .build();
    }
}
