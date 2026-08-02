package com.mr486.msnotes.mapper;

import com.mr486.commun.dto.NoteDto;
import com.mr486.msnotes.model.Note;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Convertit les notes stockées en objets de transport exposés par l'API.
 *
 * <p>Seul le contenu rédigé par le praticien est publié : l'identifiant technique, le
 * patient rattaché et la date d'enregistrement restent internes au service.</p>
 *
 * <p><b>Exemple :</b> {@code versDto(note)} retourne un {@link NoteDto} ne portant que le
 * texte de la note.</p>
 */
@Component
public class NoteMapper {

    /**
     * Convertit une note stockée en objet de transport.
     *
     * <p><b>Exemple :</b> une note « Le patient déclare qu'il fume depuis peu » devient un
     * {@link NoteDto} portant ce même texte.</p>
     *
     * @param note note issue de la base documentaire
     * @return la représentation exposée par l'API
     */
    public NoteDto versDto(Note note) {
        return NoteDto.builder()
                .content(note.getContent())
                .build();
    }

    /**
     * Convertit une liste de notes stockées en objets de transport.
     *
     * <p><b>Exemple :</b> les cinq notes d'un patient produisent une liste de cinq
     * {@link NoteDto}, dans le même ordre.</p>
     *
     * @param notes notes issues de la base documentaire
     * @return les représentations exposées par l'API, dans le même ordre
     */
    public List<NoteDto> versListeDto(List<Note> notes) {
        return notes.stream().map(this::versDto).toList();
    }
}
