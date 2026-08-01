package com.mr486.msnotes.mapper;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msnotes.model.Note;
import org.springframework.data.domain.Page;
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
     * Convertit une page de notes stockées en page d'objets de transport.
     *
     * <p>Les informations de pagination sont recopiées telles quelles, afin que le client
     * puisse construire sa navigation sans second appel.</p>
     *
     * <p><b>Exemple :</b> une page de cinq notes sur un total de douze produit un
     * {@link PageDto} dont {@code totalPages} vaut 3.</p>
     *
     * @param notes page de notes issue de la base documentaire
     * @return la page exposée par l'API, dans le même ordre
     */
    public PageDto<NoteDto> versPageDto(Page<Note> notes) {
        return PageDto.<NoteDto>builder()
                .contenu(notes.getContent().stream().map(this::versDto).toList())
                .page(notes.getNumber())
                .taille(notes.getSize())
                .totalElements(notes.getTotalElements())
                .totalPages(notes.getTotalPages())
                .build();
    }
}
