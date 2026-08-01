package com.mr486.msrisque.client;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msrisque.configuration.FeignSecurityConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-notes", configuration = FeignSecurityConfiguration.class)
public interface NoteClient {

    @GetMapping("/patients/{id}/notes")
    PageDto<NoteDto> getNotesByPatientId(
            @PathVariable Long id,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
