package com.mr486.mswebclient.service;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.mswebclient.configuration.ConstantesWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteApiService {

    private final ClientPasserelle clientPasserelle;

    public PageDto<NoteDto> listeLesNotes(Long patientId, int page) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes"
                        + "?page=" + page + "&size=" + ConstantesApplication.TAILLE_PAGE_DEFAUT,
                null,
                new ParameterizedTypeReference<PageDto<NoteDto>>() {},
                ConstantesWebclient.SERVICE_NOTES);
    }

    public NoteDto ajouteUneNote(Long patientId, NoteDto note) {
        return clientPasserelle.echange(
                HttpMethod.POST,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes",
                note,
                new ParameterizedTypeReference<NoteDto>() {},
                ConstantesWebclient.SERVICE_NOTES);
    }
}
