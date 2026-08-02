package com.mr486.mspatients.repository;

import com.mr486.mspatients.model.Patient;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Accès aux fiches patients en base relationnelle.
 *
 * <p><b>Exemple :</b> {@code findById(1L)} retourne la fiche du patient 1 si elle
 * existe.</p>
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Indique si un patient porte déjà l'identité décrite.
     *
     * <p>L'identité d'un patient est la combinaison de son nom, de son prénom, de sa date
     * de naissance et de son genre : c'est elle qui sert de contrôle d'unicité.</p>
     *
     * <p><b>Exemple :</b> pour « Dupont Jean, né le 12/05/1990, M » déjà enregistré,
     * l'appel retourne {@code true}.</p>
     *
     * @param lastName  nom de famille recherché
     * @param firstName prénom recherché
     * @param birthDate date de naissance recherchée
     * @param gender    genre recherché
     * @return {@code true} si une fiche porte déjà cette identité
     */
    boolean existsByLastNameAndFirstNameAndBirthDateAndGender(
            String lastName, String firstName, LocalDate birthDate, String gender);
}
