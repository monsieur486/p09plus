package com.mr486.msrisque.client;

import com.mr486.commun.dto.NoteDto;
import com.mr486.msrisque.configuration.FeignSecurityConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-notes", configuration = FeignSecurityConfiguration.class)
public interface NoteClient {

    @GetMapping("/patients/{id}/notes")
    List<NoteDto> getNotesByPatientId(@PathVariable Long id);
}
