package com.mr486.mspatients.mapper;

import com.mr486.commun.dto.PageDto;
import com.mr486.commun.dto.PatientDto;
import com.mr486.mspatients.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Convertit les fiches patients en objets de transport exposés par l'API.
 *
 * <p>Cette conversion isole le contrat public du schéma de persistance : l'entité JPA ne
 * franchit jamais la frontière web, et une évolution de la base n'impose pas de modifier
 * les clients.</p>
 *
 * <p><b>Exemple :</b> {@code versDto(patient)} retourne un {@link PatientDto} portant les
 * mêmes valeurs que la fiche enregistrée.</p>
 */
@Component
public class PatientMapper {

    /**
     * Convertit une fiche patient en objet de transport.
     *
     * <p><b>Exemple :</b> une fiche « Jean Dupont, né le 12/05/1990 » devient un
     * {@link PatientDto} dont {@code birthDate} vaut {@code 1990-05-12}.</p>
     *
     * @param patient fiche patient issue de la base
     * @return la représentation exposée par l'API
     */
    public PatientDto versDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .birthDate(patient.getBirthDate())
                .gender(patient.getGender())
                .postalAddress(patient.getPostalAddress())
                .phoneNumber(patient.getPhoneNumber())
                .build();
    }

    /**
     * Convertit une page de fiches patients en page d'objets de transport.
     *
     * <p>Les informations de pagination sont recopiées telles quelles, afin que le client
     * puisse construire sa navigation sans second appel.</p>
     *
     * <p><b>Exemple :</b> une page de dix patients sur un total de quarante-sept produit un
     * {@link PageDto} dont {@code totalPages} vaut 5.</p>
     *
     * @param patients page de fiches patients issue de la base
     * @return la page exposée par l'API, dans le même ordre
     */
    public PageDto<PatientDto> versPageDto(Page<Patient> patients) {
        return PageDto.<PatientDto>builder()
                .contenu(patients.getContent().stream().map(this::versDto).toList())
                .page(patients.getNumber())
                .taille(patients.getSize())
                .totalElements(patients.getTotalElements())
                .totalPages(patients.getTotalPages())
                .build();
    }
}
