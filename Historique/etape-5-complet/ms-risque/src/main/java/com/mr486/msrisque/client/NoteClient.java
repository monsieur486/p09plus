package com.mr486.msrisque.client;

import com.mr486.commun.dto.NoteDto;
import com.mr486.msrisque.configuration.FeignSecurityConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client d'accès au microservice de gestion des notes.
 *
 * <p>L'instance appelée est résolue par le registre de services à partir du nom
 * {@code ms-notes} : l'appel ne transite pas par la passerelle. L'authentification est
 * ajoutée par {@link FeignSecurityConfiguration}.</p>
 *
 * <p><b>Exemple :</b> {@code getNotesByPatientId(2L)} appelle
 * {@code GET /patients/2/notes} sur une instance disponible du service.</p>
 */
@FeignClient(name = "ms-notes", configuration = FeignSecurityConfiguration.class)
public interface NoteClient {

    /**
     * Récupère toutes les notes rattachées à un patient.
     *
     * <p><b>Exemple :</b> {@code getNotesByPatientId(9L)} retourne une liste vide si le
     * patient n'a aucune note.</p>
     *
     * @param id identifiant du patient concerné
     * @return les notes du patient, de la plus récente à la plus ancienne
     */
    @GetMapping("/patients/{id}/notes")
    List<NoteDto> getNotesByPatientId(@PathVariable Long id);
}
