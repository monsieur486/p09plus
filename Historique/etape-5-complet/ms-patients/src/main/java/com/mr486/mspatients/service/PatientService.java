package com.mr486.mspatients.service;

import com.mr486.commun.dto.PatientForm;
import com.mr486.commun.exception.DuplicateException;
import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.mspatients.model.Patient;
import com.mr486.mspatients.repository.PatientRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Gère le cycle de vie des fiches patients.
 *
 * <p>Un patient est identifié de façon unique par la combinaison nom, prénom, date de
 * naissance et genre : deux fiches portant ces quatre valeurs ne peuvent coexister.</p>
 *
 * <p><b>Exemple :</b> enregistrer une seconde fois « Jean Dupont, né le 12/05/1990, M »
 * lève une {@link DuplicateException}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Retourne toutes les fiches patients enregistrées.
     *
     * <p><b>Exemple :</b> {@code findAll()} retourne les quatre fiches de démonstration,
     * et une liste vide tant qu'aucun patient n'a été créé.</p>
     *
     * @return les fiches patients enregistrées, éventuellement vide
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Retrouve un patient par son identifiant technique.
     *
     * <p><b>Exemple :</b> {@code findById(1L)} retourne la fiche du patient 1 ; un
     * identifiant inconnu lève une {@link ResourceNotFoundException}.</p>
     *
     * @param id identifiant du patient recherché
     * @return la fiche du patient correspondant
     * @throws ResourceNotFoundException si aucun patient ne porte cet identifiant
     */
    public Patient findById(Long id) {
        return patientRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun patient avec l'id: " + id));
    }

    /**
     * Enregistre un nouveau patient après contrôle d'unicité.
     *
     * <p><b>Exemple :</b> {@code savePatient(formulaire)} retourne la fiche créée avec
     * son identifiant ; si un patient de même identité existe déjà, une
     * {@link DuplicateException} est levée et rien n'est enregistré.</p>
     *
     * @param patientForm données saisies pour le nouveau patient
     * @return la fiche patient enregistrée
     * @throws DuplicateException si un patient de même identité existe déjà
     */
    public Patient savePatient(PatientForm patientForm) {
        verifieAbsenceDeDoublon(patientForm);

        Patient patient = new Patient();
        appliqueLeFormulaire(patient, patientForm);
        Patient enregistre = patientRepository.save(patient);

        log.info("patient {} créé", enregistre.getId());
        return enregistre;
    }

    /**
     * Met à jour une fiche patient existante.
     *
     * <p>Le contrôle d'unicité n'est effectué que si le formulaire modifie l'identité du
     * patient : changer une adresse ou un numéro de téléphone ne peut donc pas être
     * refusé comme un doublon de la fiche elle-même.</p>
     *
     * <p><b>Exemple :</b> {@code updatePatient(1L, formulaire)} retourne la fiche mise à
     * jour ; renommer le patient 1 vers une identité déjà portée par une autre fiche lève
     * une {@link DuplicateException}.</p>
     *
     * @param id          identifiant du patient à mettre à jour
     * @param patientForm nouvelles données du patient
     * @return la fiche patient mise à jour
     * @throws ResourceNotFoundException si aucun patient ne porte cet identifiant
     * @throws DuplicateException        si la nouvelle identité est déjà utilisée
     */
    public Patient updatePatient(Long id, PatientForm patientForm) {
        Patient existant = findById(id);

        if (identiteModifiee(existant, patientForm)) {
            verifieAbsenceDeDoublon(patientForm);
        }
        appliqueLeFormulaire(existant, patientForm);
        Patient miseAJour = patientRepository.save(existant);

        log.info("patient {} mis à jour", id);
        return miseAJour;
    }

    // Lève une DuplicateException si un patient porte déjà l'identité décrite par le formulaire.
    private void verifieAbsenceDeDoublon(PatientForm patientForm) {
        boolean existeDeja = patientRepository.existsByLastNameAndFirstNameAndBirthDateAndGender(
                patientForm.getLastName(),
                patientForm.getFirstName(),
                patientForm.getBirthDate(),
                patientForm.getGender());

        log.debug("contrôle d'unicité effectué, doublon détecté : {}", existeDeja);
        if (existeDeja) {
            throw new DuplicateException("Le patient existe déjà dans la base de données.");
        }
    }

    // Indique si le formulaire change l'un des quatre champs identifiant le patient.
    private boolean identiteModifiee(Patient existant, PatientForm patientForm) {
        return !Objects.equals(existant.getLastName(), patientForm.getLastName())
                || !Objects.equals(existant.getFirstName(), patientForm.getFirstName())
                || !Objects.equals(existant.getBirthDate(), patientForm.getBirthDate())
                || !Objects.equals(existant.getGender(), patientForm.getGender());
    }

    // Recopie les valeurs du formulaire sur la fiche patient.
    private void appliqueLeFormulaire(Patient patient, PatientForm patientForm) {
        patient.setFirstName(patientForm.getFirstName());
        patient.setLastName(patientForm.getLastName());
        patient.setBirthDate(patientForm.getBirthDate());
        patient.setGender(patientForm.getGender());
        patient.setPostalAddress(patientForm.getPostalAddress());
        patient.setPhoneNumber(patientForm.getPhoneNumber());
    }
}
