package com.mr486.commun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Données saisies pour créer ou modifier un patient.
 *
 * <p>Ce type est le contrat d'<b>écriture</b> de l'API : il ne porte pas d'identifiant,
 * celui-ci étant transmis dans l'URL. Les quatre premiers champs sont obligatoires car
 * ils identifient le patient de façon unique.</p>
 *
 * <p><b>Exemple :</b> {@code POST /ms-patients/patients} avec
 * {@code {"firstName":"Jean","lastName":"Dupont","birthDate":"1990-05-12","gender":"M"}}
 * crée un patient ; omettre {@code lastName} retourne un statut 400.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientForm {

    /** Prénom du patient, obligatoire. */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    /** Nom de famille du patient, obligatoire. */
    @NotBlank(message = "Le nom de famille est obligatoire")
    private String lastName;

    /** Date de naissance du patient, obligatoire. */
    @NotNull(message = "La date de naissance est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /** Genre du patient, obligatoire : {@code "M"} pour masculin, {@code "F"} pour féminin. */
    @NotBlank(message = "Le genre est obligatoire")
    private String gender;

    /** Adresse postale du patient, facultative. */
    private String postalAddress;

    /** Numéro de téléphone du patient, facultatif. */
    private String phoneNumber;
}
