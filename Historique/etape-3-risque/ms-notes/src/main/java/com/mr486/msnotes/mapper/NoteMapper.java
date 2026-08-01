package com.mr486.msnotes.mapper;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msnotes.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {
    public NoteDto versDto(Note note) {
        return NoteDto.builder()
                .content(note.getContent())
                .build();
    }

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
