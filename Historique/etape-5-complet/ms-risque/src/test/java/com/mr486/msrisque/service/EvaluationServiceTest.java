package com.mr486.msrisque.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PatientDto;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests du calcul du niveau de risque de diabète.
 *
 * <p>L'horloge est figée au 1er août 2026 pour que les calculs d'âge soient
 * reproductibles quel que soit le jour d'exécution.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Évaluation du risque de diabète")
class EvaluationServiceTest {

    /** Date à laquelle l'horloge de test est figée. */
    private static final LocalDate DATE_REFERENCE = LocalDate.of(2026, 8, 1);

    /** Identifiant du patient utilisé par tous les scénarios. */
    private static final Long PATIENT_ID = 1L;

    /**
     * Termes déclencheurs écrits avec une majuscule initiale : la détection doit être
     * insensible à la casse, y compris pour « Hémoglobine A1C ».
     */
    private static final List<String> TERMES = List.of(
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeur",
            "Anormal",
            "Cholestérol",
            "Vertige",
            "Rechute",
            "Réaction",
            "Anticorps");

    @Mock
    private PatientService patientService;

    @Mock
    private NotesService notesService;

    private EvaluationService evaluationService;

    @BeforeEach
    void initialiseLeServiceAvecUneHorlogeFigee() {
        Clock horlogeFigee = Clock.fixed(
                DATE_REFERENCE.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        evaluationService = new EvaluationService(patientService, notesService, horlogeFigee);
    }

    @Test
    @DisplayName("aucun terme déclencheur dans les notes : le risque est None")
    void aucunDeclencheur_retourneNone() {
        donneUnPatient(40, "F");
        donneDesNotesAvec(0);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("None");
    }

    @Test
    @DisplayName("patient de 30 ans pile avec 2 déclencheurs : le risque est Borderline")
    void trenteAnsPileAvecDeuxDeclencheurs_retourneBorderline() {
        donneUnPatient(30, "F");
        donneDesNotesAvec(2);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Borderline");
    }

    @Test
    @DisplayName("patient de plus de 30 ans avec 5 déclencheurs : le risque est Borderline")
    void plusDeTrenteAnsAvecCinqDeclencheurs_retourneBorderline() {
        donneUnPatient(40, "M");
        donneDesNotesAvec(5);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Borderline");
    }

    @Test
    @DisplayName("patient de plus de 30 ans avec 6 déclencheurs : le risque est In Danger")
    void plusDeTrenteAnsAvecSixDeclencheurs_retourneInDanger() {
        donneUnPatient(40, "M");
        donneDesNotesAvec(6);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    @Test
    @DisplayName("patient de plus de 30 ans avec 8 déclencheurs : le risque est Early onset")
    void plusDeTrenteAnsAvecHuitDeclencheurs_retourneEarlyOnset() {
        donneUnPatient(40, "F");
        donneDesNotesAvec(8);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Early onset");
    }

    @Test
    @DisplayName("homme de moins de 30 ans avec 3 déclencheurs : le risque est In Danger")
    void hommeJeuneAvecTroisDeclencheurs_retourneInDanger() {
        donneUnPatient(25, "M");
        donneDesNotesAvec(3);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    @Test
    @DisplayName("homme de moins de 30 ans avec 4 déclencheurs : le risque reste In Danger")
    void hommeJeuneAvecQuatreDeclencheurs_retourneInDanger() {
        donneUnPatient(25, "M");
        donneDesNotesAvec(4);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    @Test
    @DisplayName("homme de moins de 30 ans avec 5 déclencheurs : le risque est Early onset")
    void hommeJeuneAvecCinqDeclencheurs_retourneEarlyOnset() {
        donneUnPatient(25, "M");
        donneDesNotesAvec(5);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Early onset");
    }

    @Test
    @DisplayName("femme de moins de 30 ans avec 4 déclencheurs : le risque est In Danger")
    void femmeJeuneAvecQuatreDeclencheurs_retourneInDanger() {
        donneUnPatient(25, "F");
        donneDesNotesAvec(4);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    @Test
    @DisplayName("femme de moins de 30 ans avec 7 déclencheurs : le risque est Early onset")
    void femmeJeuneAvecSeptDeclencheurs_retourneEarlyOnset() {
        donneUnPatient(25, "F");
        donneDesNotesAvec(7);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Early onset");
    }

    @Test
    @DisplayName("patient jeune avec 2 déclencheurs : Borderline ne s'applique pas, le risque est None")
    void patientJeuneAvecDeuxDeclencheurs_retourneNone() {
        donneUnPatient(25, "F");
        donneDesNotesAvec(2);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("None");
    }

    @Test
    @DisplayName("« Hémoglobine A1C » écrit avec ses majuscules est bien détecté")
    void declencheurAvecMajuscules_estDetecte() {
        donneUnPatient(40, "M");
        when(notesService.getNotesByPatientId(PATIENT_ID)).thenReturn(List.of(
                new NoteDto("Hémoglobine A1C supérieure au niveau recommandé"),
                new NoteDto("Microalbumine détectée")));

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("Borderline");
    }

    @Test
    @DisplayName("les déclencheurs répartis sur plusieurs notes sont cumulés")
    void declencheursRepartisSurPlusieursNotes_sontCumules() {
        donneUnPatient(40, "M");
        when(notesService.getNotesByPatientId(PATIENT_ID)).thenReturn(List.of(
                new NoteDto("Taille et Poids relevés"),
                new NoteDto("Patient Fumeur"),
                new NoteDto("Cholestérol élevé"),
                new NoteDto("Vertige signalé"),
                new NoteDto("Réaction aux médicaments")));

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    @Test
    @DisplayName("un anniversaire non encore passé cette année ne compte pas dans l'âge")
    void anniversaireNonEncorePasse_neCompteQueLesAnneesRevolues() {
        // Né le 2 août 1996 : au 1er août 2026 le patient a 29 ans révolus, pas 30.
        when(patientService.getPatientById(PATIENT_ID)).thenReturn(new PatientDto(
                PATIENT_ID, "Test", "Patient", DATE_REFERENCE.minusYears(30).plusDays(1),
                "M", null, null));
        donneDesNotesAvec(3);

        assertThat(evaluationService.evaluationDuRisque(PATIENT_ID)).isEqualTo("In Danger");
    }

    // Prépare un patient d'âge et de genre donnés, dont l'anniversaire tombe le jour de référence.
    private void donneUnPatient(int age, String genre) {
        when(patientService.getPatientById(PATIENT_ID)).thenReturn(new PatientDto(
                PATIENT_ID, "Test", "Patient", DATE_REFERENCE.minusYears(age), genre, null, null));
    }

    // Prépare une note unique contenant les N premiers termes déclencheurs.
    private void donneDesNotesAvec(int nombreDeDeclencheurs) {
        String contenu = String.join(", ", TERMES.subList(0, nombreDeDeclencheurs));
        when(notesService.getNotesByPatientId(PATIENT_ID)).thenReturn(List.of(new NoteDto(contenu)));
    }
}
