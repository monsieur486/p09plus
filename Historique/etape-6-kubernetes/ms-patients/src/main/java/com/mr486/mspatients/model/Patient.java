package com.mr486.mspatients.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Fiche patient telle qu'elle est enregistrée en base.
 *
 * <p>Cette entité reste confinée à la couche de persistance : les API exposent le
 * {@code PatientDto} de la bibliothèque commune. Le schéma correspondant est créé par
 * Liquibase, jamais par Hibernate.</p>
 *
 * <p><b>Exemple :</b> une fiche « Jean Dupont, né le 12/05/1990, M » occupe une ligne de
 * la table {@code patients}.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "patients")
// creedengo demande de rendre ces champs finaux ; c'est impossible pour une entité JPA.
// Hibernate instancie l'entité par son constructeur sans argument puis affecte les champs,
// et l'identifiant n'est attribué qu'à l'insertion — un champ final l'en empêcherait.
@SuppressWarnings("creedengo-java:GCI82")
public class Patient {

    /** Identifiant technique, attribué par la base à l'enregistrement. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prénom du patient. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** Nom de famille du patient. */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** Date de naissance, utilisée pour calculer l'âge lors de l'évaluation du risque. */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /** Genre du patient : {@code "M"} pour masculin, {@code "F"} pour féminin. */
    @Column(name = "gender", nullable = false)
    private String gender;

    /** Adresse postale du patient, facultative. */
    @Column(name = "postal_address")
    private String postalAddress;

    /** Numéro de téléphone du patient, facultatif. */
    @Column(name = "phone_number")
    private String phoneNumber;
}
