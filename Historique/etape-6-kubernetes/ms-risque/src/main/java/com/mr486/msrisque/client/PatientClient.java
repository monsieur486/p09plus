package com.mr486.msrisque.client;

import com.mr486.commun.dto.PatientDto;
import com.mr486.msrisque.configuration.FeignSecurityConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client d'accès au microservice de gestion des patients.
 *
 * <p>L'instance appelée est résolue par le registre de services à partir du nom
 * {@code ms-patients} : l'appel ne transite pas par la passerelle. L'authentification est
 * ajoutée par {@link FeignSecurityConfiguration}.</p>
 *
 * <p><b>Exemple :</b> {@code findById(1L)} appelle {@code GET /patients/1} sur une instance
 * disponible du service des patients.</p>
 */
@FeignClient(name = "ms-patients", configuration = FeignSecurityConfiguration.class)
public interface PatientClient {

    /**
     * Récupère la fiche d'un patient auprès du microservice des patients.
     *
     * <p><b>Exemple :</b> {@code findById(42L)} produit une erreur 404 côté client si aucune
     * fiche ne porte cet identifiant.</p>
     *
     * @param id identifiant du patient recherché
     * @return la fiche du patient
     */
    @GetMapping("/patients/{id}")
    PatientDto findById(final @PathVariable("id") Long id);
}
