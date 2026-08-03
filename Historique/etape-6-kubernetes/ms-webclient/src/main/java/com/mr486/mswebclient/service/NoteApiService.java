package com.mr486.mswebclient.service;

import com.mr486.commun.dto.NoteDto;
import com.mr486.mswebclient.configuration.ConstantesWebclient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Consulte et enrichit les notes d'un patient auprès du microservice dédié.
 *
 * <p><b>Exemple :</b> {@code listeLesNotes(2L)} retourne les notes affichées sur la fiche
 * du patient 2, de la plus récente à la plus ancienne.</p>
 */
@Service
@RequiredArgsConstructor
public class NoteApiService {

    private final ClientPasserelle clientPasserelle;

    /**
     * Retourne les notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code listeLesNotes(9L)} retourne une liste vide si aucune note
     * n'a été saisie pour ce patient.</p>
     *
     * @param patientId identifiant du patient concerné
     * @return les notes du patient
     */
    public List<NoteDto> listeLesNotes(final Long patientId) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes",
                null,
                new ParameterizedTypeReference<List<NoteDto>>() {},
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
    public NoteDto ajouteUneNote(final Long patientId, final NoteDto note) {
        return clientPasserelle.echange(
                HttpMethod.POST,
                ConstantesWebclient.CHEMIN_NOTES + patientId + "/notes",
                note,
                new ParameterizedTypeReference<NoteDto>() {},
                ConstantesWebclient.SERVICE_NOTES);
    }
}
