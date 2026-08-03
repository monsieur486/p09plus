package com.mr486.mswebclient.service;

import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.dto.PatientForm;
import com.mr486.mswebclient.configuration.ConstantesWebclient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * Consulte et met à jour les fiches patients auprès du microservice dédié.
 *
 * <p>Cette couche isole les contrôleurs du protocole d'appel : ils manipulent des objets
 * métier sans connaître ni la passerelle, ni les chemins des API.</p>
 *
 * <p><b>Exemple :</b> {@code listeLesPatients()} retourne les fiches à afficher dans le
 * tableau de bord.</p>
 */
@Service
@RequiredArgsConstructor
public class PatientApiService {

    private final ClientPasserelle clientPasserelle;

    /**
     * Retourne toutes les fiches patients.
     *
     * <p><b>Exemple :</b> {@code listeLesPatients()} retourne une liste vide tant qu'aucun
     * patient n'a été créé.</p>
     *
     * @return les fiches patients
     */
    public List<PatientDto> listeLesPatients() {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_PATIENTS,
                null,
                new ParameterizedTypeReference<List<PatientDto>>() {},
                ConstantesWebclient.SERVICE_PATIENTS);
    }

    /**
     * Retourne la fiche d'un patient désigné par son identifiant.
     *
     * <p><b>Exemple :</b> {@code recupereLePatient(1L)} retourne la fiche affichée sur la
     * page de détail.</p>
     *
     * @param id identifiant du patient recherché
     * @return la fiche du patient
     */
    public PatientDto recupereLePatient(final Long id) {
        return clientPasserelle.echange(
                HttpMethod.GET,
                ConstantesWebclient.CHEMIN_PATIENTS + "/" + id,
                null,
                new ParameterizedTypeReference<PatientDto>() {},
                ConstantesWebclient.SERVICE_PATIENTS);
    }

    /**
     * Crée un patient à partir des données saisies dans le formulaire.
     *
     * <p><b>Exemple :</b> {@code creeLePatient(formulaire)} retourne la fiche créée, dotée
     * de son identifiant.</p>
     *
     * @param formulaire données saisies pour le nouveau patient
     * @return la fiche créée
     */
    public PatientDto creeLePatient(final PatientForm formulaire) {
        return clientPasserelle.echange(
                HttpMethod.POST,
                ConstantesWebclient.CHEMIN_PATIENTS,
                formulaire,
                new ParameterizedTypeReference<PatientDto>() {},
                ConstantesWebclient.SERVICE_PATIENTS);
    }

    /**
     * Met à jour un patient existant.
     *
     * <p><b>Exemple :</b> {@code metAJourLePatient(1L, formulaire)} retourne la fiche
     * modifiée.</p>
     *
     * @param id         identifiant du patient à mettre à jour
     * @param formulaire nouvelles données du patient
     * @return la fiche mise à jour
     */
    public PatientDto metAJourLePatient(final Long id, final PatientForm formulaire) {
        return clientPasserelle.echange(
                HttpMethod.PUT,
                ConstantesWebclient.CHEMIN_PATIENTS + "/" + id,
                formulaire,
                new ParameterizedTypeReference<PatientDto>() {},
                ConstantesWebclient.SERVICE_PATIENTS);
    }
}
