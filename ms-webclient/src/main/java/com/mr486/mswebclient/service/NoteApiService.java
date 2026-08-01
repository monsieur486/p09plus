package com.mr486.mswebclient.service;

import com.mr486.commun.configuration.ConstantesApplication;
import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.mswebclient.configuration.ConstantesWebclient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Consulte et enrichit les notes d'un patient auprès du microservice dédié.
 *
 * <p><b>Exemple :</b> {@code listeLesNotes(2L)} retourne les notes affichées sur la page
 * du patient 2, de la plus récente à la plus ancienne.</p>
 */
@Service
@RequiredArgsConstructor
public class NoteApiService {

    private final ClientPasserelle clientPasserelle;

    /**
     * Retourne une page de notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code listeLesNotes(9L, 0)} retourne une page vide si aucune
     * note n'a été saisie pour ce patient.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param page      numéro de la page demandée, à partir de zéro
     * @return la page de notes du patient
     */
    public PageDto<NoteDto> listeLesNotes(Long patientId, int page) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes"
                        + "?page=" + page + "&size=" + ConstantesApplication.TAILLE_PAGE_DEFAUT,
                null,
                new ParameterizedTypeReference<PageDto<NoteDto>>() {},
                ConstantesWebclient.SERVICE_NOTES);
    }

    /**
     * Ajoute une note à un patient.
     *
     * <p><b>Exemple :</b> {@code ajouteUneNote(2L, note)} retourne la note enregistrée.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param note      contenu de la note à ajouter
     * @return la note enregistrée
     */
    public NoteDto ajouteUneNote(Long patientId, NoteDto note) {
        return clientPasserelle.echange(
                HttpMethod.POST,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes",
                note,
                new ParameterizedTypeReference<NoteDto>() {},
                ConstantesWebclient.SERVICE_NOTES);
    }
}
