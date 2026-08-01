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

    /**
     * Retourne une page de notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p>La lecture est paginée : le nombre de notes d'un patient suivi de longue date
     * n'est pas borné, et tout ramener en une fois dégraderait l'affichage de sa fiche.</p>
     *
     * <p><b>Exemple :</b> {@code findByPatientId(9L, pagination)} retourne une page vide si
     * aucune note n'a été saisie pour ce patient.</p>
     *
     * @param patientId  identifiant du patient concerné
     * @param pagination page demandée et nombre d'éléments par page
     * @return la page de notes du patient, éventuellement vide
     */
    public Page<Note> findByPatientId(Long patientId, Pageable pagination) {
        Page<Note> notes = noteRepository.findByPatientIdOrderByCreatedDateDesc(patientId, pagination);
        if (notes.isEmpty()) {
            log.warn("aucune note enregistrée pour le patient {}", patientId);
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
    public Note save(Long patientId, NoteDto noteDto) {
        Note enregistree = noteRepository.save(construitNote(patientId, noteDto));
        log.info("note créée pour le patient {}", patientId);
        return enregistree;
    }

    // Assemble la note à persister ; le contenu n'est pas journalisé car il relève du secret médical.
    private Note construitNote(Long patientId, NoteDto noteDto) {
        log.debug("construction d'une note pour le patient {}", patientId);
        return Note.builder()
                .patientId(patientId)
                .content(noteDto.getContent())
                .createdDate(Instant.now())
                .build();
    }
}
