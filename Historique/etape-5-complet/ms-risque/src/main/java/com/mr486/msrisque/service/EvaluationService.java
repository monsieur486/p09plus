package com.mr486.msrisque.service;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PatientDto;
import com.mr486.msrisque.configuration.ConstantesRisque;
import com.mr486.msrisque.model.NiveauRisque;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Calcule le niveau de risque de diabète d'un patient.
 *
 * <p>Le calcul croise trois éléments : le nombre de termes déclencheurs relevés dans les
 * notes du praticien, l'âge du patient et son genre. Les niveaux sont évalués du plus
 * grave au moins grave, de sorte qu'un nombre de déclencheurs plus élevé ne puisse jamais
 * aboutir à un risque moindre.</p>
 *
 * <p><b>Exemple :</b> un homme de 25 ans dont les notes contiennent « Taille », « Poids »
 * et « Fumeur » totalise 3 déclencheurs et ressort {@code In Danger}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {

    private final PatientService patientService;
    private final NotesService notesService;
    private final Clock horloge;

    /**
     * Évalue le niveau de risque de diabète d'un patient à partir de ses notes.
     *
     * <p><b>Exemple :</b> {@code evaluationDuRisque(4L)} retourne {@code "Early onset"}
     * pour un patient de moins de 30 ans cumulant au moins 5 déclencheurs.</p>
     *
     * @param patientId identifiant du patient à évaluer
     * @return le libellé du niveau de risque : None, Borderline, In Danger ou Early onset
     */
    public String evaluationDuRisque(Long patientId) {
        PatientDto patient = patientService.getPatientById(patientId);
        List<NoteDto> notes = notesService.getNotesByPatientId(patientId);

        int age = calculeAge(patient.getBirthDate());
        int nombreDeDeclencheurs = compteDeclencheurs(notes);
        NiveauRisque niveauRisque = determineNiveau(age, patient.getGender(), nombreDeDeclencheurs);

        log.info("évaluation du risque calculée pour le patient {}", patientId);
        return niveauRisque.getLibelle();
    }

    // Compte les termes déclencheurs présents dans les notes, sans tenir compte de la casse.
    private int compteDeclencheurs(List<NoteDto> notes) {
        int nombreDeDeclencheurs = 0;
        for (NoteDto note : notes) {
            String contenu = note.getContent().toLowerCase();
            for (String terme : ConstantesRisque.TERMES_DECLENCHEURS) {
                if (contenu.contains(terme)) {
                    nombreDeDeclencheurs++;
                }
            }
        }
        log.debug("{} déclencheur(s) relevé(s) dans {} note(s)", nombreDeDeclencheurs, notes.size());
        return nombreDeDeclencheurs;
    }

    // Calcule l'âge en années révolues : un anniversaire non encore passé ne compte pas.
    private int calculeAge(LocalDate dateDeNaissance) {
        return Period.between(dateDeNaissance, LocalDate.now(horloge)).getYears();
    }

    // Aiguille vers les règles applicables selon que le patient a moins de 30 ans ou non.
    private NiveauRisque determineNiveau(int age, String genre, int nombreDeDeclencheurs) {
        if (nombreDeDeclencheurs == 0) {
            return NiveauRisque.NONE;
        }
        if (age < ConstantesRisque.AGE_PIVOT) {
            return niveauPatientJeune(genre, nombreDeDeclencheurs);
        }
        return niveauPatientTrenteEtPlus(nombreDeDeclencheurs);
    }

    // Règles des patients de moins de 30 ans : les seuils diffèrent selon le genre.
    private NiveauRisque niveauPatientJeune(String genre, int nombreDeDeclencheurs) {
        boolean masculin = ConstantesRisque.GENRE_MASCULIN.equalsIgnoreCase(genre);
        int seuilEarlyOnset = masculin
                ? ConstantesRisque.SEUIL_EARLY_ONSET_HOMME_JEUNE
                : ConstantesRisque.SEUIL_EARLY_ONSET_FEMME_JEUNE;
        int seuilInDanger = masculin
                ? ConstantesRisque.SEUIL_IN_DANGER_HOMME_JEUNE
                : ConstantesRisque.SEUIL_IN_DANGER_FEMME_JEUNE;

        if (nombreDeDeclencheurs >= seuilEarlyOnset) {
            return NiveauRisque.EARLY_ONSET;
        }
        if (nombreDeDeclencheurs >= seuilInDanger) {
            return NiveauRisque.IN_DANGER;
        }
        return NiveauRisque.NONE;
    }

    // Règles des patients de 30 ans et plus : seuls les seuils de déclencheurs comptent.
    private NiveauRisque niveauPatientTrenteEtPlus(int nombreDeDeclencheurs) {
        if (nombreDeDeclencheurs >= ConstantesRisque.SEUIL_EARLY_ONSET_TRENTE_ET_PLUS) {
            return NiveauRisque.EARLY_ONSET;
        }
        if (nombreDeDeclencheurs >= ConstantesRisque.SEUIL_IN_DANGER_TRENTE_ET_PLUS) {
            return NiveauRisque.IN_DANGER;
        }
        if (nombreDeDeclencheurs >= ConstantesRisque.SEUIL_BORDERLINE) {
            return NiveauRisque.BORDERLINE;
        }
        return NiveauRisque.NONE;
    }
}
