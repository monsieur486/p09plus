package com.mr486.msrisque.client;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.msrisque.configuration.FeignSecurityConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client d'accès au microservice de gestion des notes.
 *
 * <p>L'instance appelée est résolue par le registre de services à partir du nom
 * {@code ms-notes} : l'appel ne transite pas par la passerelle. L'authentification est
 * ajoutée par {@link FeignSecurityConfiguration}.</p>
 *
 * <p><b>Exemple :</b> {@code getNotesByPatientId(2L, 0, 100)} appelle
 * {@code GET /patients/2/notes?page=0&size=100} sur une instance disponible du service.</p>
 */
@FeignClient(name = "ms-notes", configuration = FeignSecurityConfiguration.class)
public interface NoteClient {

    /**
     * Récupère une page de notes rattachées à un patient.
     *
     * <p>L'API des notes étant paginée, l'évaluation du risque parcourt les pages jusqu'à
     * les avoir toutes lues : un décompte partiel sous-estimerait le risque.</p>
     *
     * <p><b>Exemple :</b> {@code getNotesByPatientId(9L, 0, 100)} retourne une page vide si
     * le patient n'a aucune note.</p>
     *
     * @param id   identifiant du patient concerné
     * @param page numéro de la page demandée, à partir de zéro
     * @param size nombre de notes par page
     * @return la page de notes du patient, de la plus récente à la plus ancienne
     */
    @GetMapping("/patients/{id}/notes")
    PageDto<NoteDto> getNotesByPatientId(
            @PathVariable Long id,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
