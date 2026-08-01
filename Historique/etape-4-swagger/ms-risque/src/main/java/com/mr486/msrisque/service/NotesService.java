package com.mr486.msrisque.service;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.NoteClient;
import com.mr486.msrisque.configuration.ConstantesRisque;
import feign.FeignException;
import java.util.ArrayList;
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
            return litToutesLesPages(patientId);
        } catch (FeignException.NotFound ex) {
            log.warn("aucune note retournée pour le patient {}", patientId);
            return List.of();
        } catch (FeignException ex) {
            throw new ServiceIndisponibleException(ConstantesRisque.SERVICE_NOTES, ex);
        }
    }

    private List<NoteDto> litToutesLesPages(Long patientId) {
        List<NoteDto> toutesLesNotes = new ArrayList<>();
        int pageCourante = 0;
        int totalDePages;

        do {
            PageDto<NoteDto> page = noteClient.getNotesByPatientId(
                    patientId, pageCourante, ConstantesApplication.TAILLE_PAGE_MAXIMALE);
            if (page == null || page.getContenu() == null) {
                break;
            }
            toutesLesNotes.addAll(page.getContenu());
            totalDePages = page.getTotalPages();
            pageCourante++;
        } while (pageCourante < totalDePages);

        log.debug("{} note(s) rassemblée(s) pour le patient {}", toutesLesNotes.size(), patientId);
        return toutesLesNotes;
    }
}
