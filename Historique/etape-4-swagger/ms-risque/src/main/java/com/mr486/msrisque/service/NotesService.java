package com.mr486.msrisque.service;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.NoteClient;
import com.mr486.msrisque.configuration.ConstantesRisque;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesService {

    private final NoteClient noteClient;

    public List<NoteDto> getNotesByPatientId(Long patientId) {
        try {
            List<NoteDto> notes = noteClient.getNotesByPatientId(patientId);
            if (notes == null) {
                log.warn("réponse sans corps du service des notes pour le patient {}", patientId);
                return List.of();
            }
            log.debug("{} note(s) rassemblée(s) pour le patient {}", notes.size(), patientId);
            return notes;
        } catch (FeignException.NotFound ex) {
            log.warn("aucune note retournée pour le patient {}", patientId);
            return List.of();
        } catch (FeignException ex) {
            throw new ServiceIndisponibleException(ConstantesRisque.SERVICE_NOTES, ex);
        }
    }
}
