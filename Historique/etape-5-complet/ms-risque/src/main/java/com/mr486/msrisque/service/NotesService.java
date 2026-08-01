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

/**
 * Récupère les notes d'un patient auprès du microservice dédié.
 *
 * <p>L'API des notes étant paginée, ce service en parcourt toutes les pages : l'évaluation
 * compte les termes déclencheurs sur l'intégralité du dossier, et s'arrêter à la première
 * page produirait un risque faussement rassurant.</p>
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

    // Parcourt les pages de notes jusqu'à la dernière et rassemble leur contenu.
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
