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

/**
 * Récupère les notes d'un patient auprès du microservice dédié.
 *
 * <p>L'évaluation compte les termes déclencheurs sur l'intégralité du dossier : ce service
 * remonte donc toutes les notes du patient, un décompte partiel produisant un risque
 * faussement rassurant.</p>
 *
 * <p>Un patient sans note n'est pas une anomalie : l'appel retourne alors une liste vide.
 * En revanche, un service arrêté ou injoignable devient une
 * {@link ServiceIndisponibleException}, pour la même raison.</p>
 *
 * <p><b>Exemple :</b> {@code getNotesByPatientId(2L)} retourne les notes du patient 2 ; si
 * le service des notes est arrêté, l'appel lève une {@link ServiceIndisponibleException}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotesService {

    private final NoteClient noteClient;

    /**
     * Retourne toutes les notes rattachées à un patient.
     *
     * <p><b>Exemple :</b> {@code getNotesByPatientId(9L)} retourne une liste vide si aucune
     * note n'a été saisie pour ce patient.</p>
     *
     * @param patientId identifiant du patient concerné
     * @return les notes du patient, éventuellement vide
     * @throws ServiceIndisponibleException si le microservice des notes ne répond pas
     */
    public List<NoteDto> getNotesByPatientId(final Long patientId) {
        try {
            final List<NoteDto> notes = noteClient.getNotesByPatientId(patientId);
            if (notes == null) {
                log.warn("réponse sans corps du service des notes pour le patient {}", patientId);
                return List.of();
            }
            log.debug("{} note(s) rassemblée(s) pour le patient {}", notes.size(), patientId);
            return notes;
        } catch (final FeignException.NotFound ex) {
            log.warn("aucune note retournée pour le patient {}", patientId);
            return List.of();
        } catch (final FeignException ex) {
            throw new ServiceIndisponibleException(ConstantesRisque.SERVICE_NOTES, ex);
        }
    }
}
