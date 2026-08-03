package com.mr486.mspatients.mapper;

import com.mr486.commun.dto.PatientDto;
import com.mr486.mspatients.model.Patient;
import java.util.List;
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
    public PatientDto versDto(final Patient patient) {
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
     * Convertit une liste de fiches patients en objets de transport.
     *
     * <p><b>Exemple :</b> les quatre fiches de démonstration produisent une liste de
     * quatre {@link PatientDto}, dans le même ordre.</p>
     *
     * @param patients fiches patients issues de la base
     * @return les représentations exposées par l'API, dans le même ordre
     */
    public List<PatientDto> versListeDto(final List<Patient> patients) {
        return patients.stream().map(this::versDto).toList();
    }
}
