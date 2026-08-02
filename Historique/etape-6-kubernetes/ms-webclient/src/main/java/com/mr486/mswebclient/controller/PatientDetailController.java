package com.mr486.mswebclient.controller;

import com.mr486.commun.dto.PatientDto;
import com.mr486.commun.dto.PatientForm;
import com.mr486.mswebclient.configuration.ConstantesToast;
import com.mr486.mswebclient.service.EvaluationApiService;
import com.mr486.mswebclient.service.NoteApiService;
import com.mr486.mswebclient.service.PatientApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Affiche la fiche complète d'un patient et son formulaire de modification.
 *
 * <p>La fiche rassemble en une seule page l'état civil, le niveau de risque évalué et les
 * notes du patient : le praticien dispose ainsi de tout le dossier sans naviguer entre
 * plusieurs écrans.</p>
 *
 * <p><b>Exemple :</b> {@code GET /app/dashboard/1} affiche la fiche du patient 1 avec ses
 * notes.</p>
 */
@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class PatientDetailController {

    /** Vue du formulaire de modification, réaffichée telle quelle en cas de saisie invalide. */
    private static final String VUE_MODIFICATION = "patient-update";

    /** Attribut portant l'identifiant du patient dans les vues. */
    private static final String ATTRIBUT_ID = "patientId";

    private final PatientApiService patientApiService;
    private final NoteApiService noteApiService;
    private final EvaluationApiService evaluationApiService;

    /**
     * Affiche la fiche complète d'un patient : état civil, risque et notes.
     *
     * <p><b>Exemple :</b> {@code GET /app/dashboard/1} affiche la fiche du patient 1 avec
     * ses notes et son niveau de risque.</p>
     *
     * @param id    identifiant du patient à afficher
     * @param model modèle transmis à la vue
     * @return le nom de la vue de détail
     */
    @GetMapping("/dashboard/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientApiService.recupereLePatient(id));
        model.addAttribute("evaluation", evaluationApiService.evalueLeRisque(id));
        model.addAttribute("notes", noteApiService.listeLesNotes(id));
        model.addAttribute(ATTRIBUT_ID, id);
        return "patient-detail";
    }

    /**
     * Affiche le formulaire de modification, prérempli avec la fiche existante.
     *
     * <p><b>Exemple :</b> {@code GET /app/dashboard/1/update} affiche les valeurs actuelles
     * du patient 1, prêtes à être corrigées.</p>
     *
     * @param id    identifiant du patient à modifier
     * @param model modèle transmis à la vue
     * @return le nom de la vue du formulaire de modification
     */
    @GetMapping("/dashboard/{id}/update")
    public String updatePatientForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", versFormulaire(patientApiService.recupereLePatient(id)));
        model.addAttribute(ATTRIBUT_ID, id);
        return VUE_MODIFICATION;
    }

    /**
     * Enregistre les modifications apportées à un patient.
     *
     * <p><b>Exemple :</b> une saisie incomplète réaffiche le formulaire avec les messages
     * d'erreur ; une saisie valide redirige vers la fiche du patient.</p>
     *
     * @param id                identifiant du patient à modifier
     * @param patient           données saisies dans le formulaire
     * @param resultatDeLiaison résultat de la validation du formulaire
     * @param model             modèle transmis à la vue
     * @param redirection       porteur du message affiché après la redirection
     * @return la redirection vers la fiche, ou le formulaire en cas d'erreur
     */
    @PostMapping("/dashboard/{id}/update")
    public String updatePatient(
            @PathVariable Long id,
            @Valid @ModelAttribute("patient") PatientForm patient,
            BindingResult resultatDeLiaison,
            Model model,
            RedirectAttributes redirection) {
        if (resultatDeLiaison.hasErrors()) {
            model.addAttribute(ATTRIBUT_ID, id);
            return VUE_MODIFICATION;
        }
        patientApiService.metAJourLePatient(id, patient);
        redirection.addFlashAttribute(ConstantesToast.CLE_NIVEAU, ConstantesToast.NIVEAU_INFO);
        redirection.addFlashAttribute(ConstantesToast.CLE_MESSAGE, "Fiche patient mise à jour.");
        return "redirect:/app/dashboard/" + id;
    }

    // Convertit la fiche consultée en formulaire modifiable ; l'identifiant reste porté par l'URL.
    private PatientForm versFormulaire(PatientDto patient) {
        return PatientForm.builder()
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .birthDate(patient.getBirthDate())
                .gender(patient.getGender())
                .postalAddress(patient.getPostalAddress())
                .phoneNumber(patient.getPhoneNumber())
                .build();
    }
}
