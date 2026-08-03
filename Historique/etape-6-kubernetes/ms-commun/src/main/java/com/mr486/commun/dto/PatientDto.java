package com.mr486.commun.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Représentation d'un patient telle qu'elle transite entre les microservices.
 *
 * <p>Ce type est le contrat de <b>lecture</b> de l'API : il expose l'identifiant du
 * patient, contrairement à {@link PatientForm} qui décrit les données d'écriture.</p>
 *
 * <p><b>Exemple :</b> un appel à {@code GET /ms-patients/patients/1} retourne un
 * {@code PatientDto} dont le champ {@code gender} vaut {@code "M"} ou {@code "F"}.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// creedengo demande de rendre ces champs finaux ; c'est impossible ici. Lombok génère un
// constructeur sans argument et des mutateurs, qu'un champ final interdirait — la classe ne
// compilerait plus. La désérialisation JSON exige d'ailleurs ces mêmes mutateurs.
@SuppressWarnings("creedengo-java:GCI82")
public class PatientDto {

    /** Identifiant technique du patient, attribué par le service de gestion des patients. */
    private Long id;

    /** Prénom du patient. */
    private String firstName;

    /** Nom de famille du patient. */
    private String lastName;

    /** Date de naissance, utilisée pour calculer l'âge lors de l'évaluation du risque. */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /** Genre du patient : {@code "M"} pour masculin, {@code "F"} pour féminin. */
    private String gender;

    /** Adresse postale du patient, facultative. */
    private String postalAddress;

    /** Numéro de téléphone du patient, facultatif. */
    private String phoneNumber;
}
