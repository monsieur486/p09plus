package com.mr486.msnotes.service;

import com.mr486.commun.dto.NoteDto;
import com.mr486.msnotes.model.Note;
import com.mr486.msnotes.repository.NoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Gère les notes rédigées par les praticiens au sujet des patients.
 *
 * <p>Les notes sont conservées dans leur ordre chronologique inverse : la plus récente
 * est restituée en premier.</p>
 *
 * <p><b>Exemple :</b> {@code findByPatientId(2L)} retourne les notes du patient 2, de la
 * plus récente à la plus ancienne.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    private final Clock horloge;

    /**
     * Retourne les notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code findByPatientId(9L)} retourne une liste vide si aucune
     * note n'a été saisie pour ce patient.</p>
     *
     * @param patientId identifiant du patient concerné
     * @return les notes du patient, éventuellement vide
     */
    public List<Note> findByPatientId(final Long patientId) {
        final List<Note> notes = noteRepository.findByPatientIdOrderByCreatedDateDesc(patientId);
        if (notes.isEmpty()) {
            log.debug("aucune note enregistrée pour le patient {}", patientId);
        }
        return notes;
    }

    /**
     * Enregistre une nouvelle note pour un patient.
     *
     * <p><b>Exemple :</b> {@code save(1L, noteDto)} retourne la note enregistrée, datée de
     * l'instant de création.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param noteDto   contenu de la note à enregistrer
     * @return la note enregistrée
     */
    public Note save(final Long patientId, final NoteDto noteDto) {
        final Note enregistree = noteRepository.save(construitNote(patientId, noteDto));
        log.info("note créée pour le patient {}", patientId);
        return enregistree;
    }

    // Assemble la note à persister ; le contenu n'est pas journalisé car il relève du secret médical.
    private Note construitNote(final Long patientId, final NoteDto noteDto) {
        log.debug("construction d'une note pour le patient {}", patientId);
        return Note.builder()
                .patientId(patientId)
                .content(noteDto.getContent())
                .createdDate(Instant.now(horloge))
                .build();
    }
}
