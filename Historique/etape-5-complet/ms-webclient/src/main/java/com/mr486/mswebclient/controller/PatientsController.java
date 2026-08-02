package com.mr486.mswebclient.controller;

import com.mr486.commun.dto.PatientForm;
import com.mr486.mswebclient.configuration.ConstantesToast;
import com.mr486.mswebclient.service.PatientApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Affiche la liste des patients et le formulaire de création.
 *
 * <p><b>Exemple :</b> {@code GET /app/dashboard} affiche le tableau de bord ;
 * {@code POST /app/dashboard/ajout} enregistre un patient puis y redirige avec un message
 * de confirmation.</p>
 */
@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class PatientsController {

    /** Vue du formulaire de création, réaffichée telle quelle en cas de saisie invalide. */
    private static final String VUE_AJOUT = "patient-ajout";

    private final PatientApiService patientApiService;

    /**
     * Affiche la liste des patients.
     *
     * <p><b>Exemple :</b> {@code GET /app/dashboard} affiche le tableau de bord, vide tant
     * qu'aucun patient n'a été créé.</p>
     *
     * @param model modèle transmis à la vue
     * @return le nom de la vue listant les patients
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("patients", patientApiService.listeLesPatients());
        return "patients";
    }

    /**
     * Affiche le formulaire de création d'un patient.
     *
     * <p><b>Exemple :</b> {@code GET /app/dashboard/ajout} affiche un formulaire vierge.</p>
     *
     * @param model modèle transmis à la vue
     * @return le nom de la vue du formulaire
     */
    @GetMapping("/dashboard/ajout")
    public String showCreatePatientForm(Model model) {
        model.addAttribute("patient", new PatientForm());
        return VUE_AJOUT;
    }

    /**
     * Enregistre un nouveau patient.
     *
     * <p><b>Exemple :</b> une saisie incomplète réaffiche le formulaire avec les messages
     * d'erreur ; une saisie valide redirige vers le tableau de bord.</p>
     *
     * @param patient           données saisies dans le formulaire
     * @param resultatDeLiaison résultat de la validation du formulaire
     * @param redirection       porteur du message affiché après la redirection
     * @return la redirection vers le tableau de bord, ou le formulaire en cas d'erreur
     */
    @PostMapping("/dashboard/ajout")
    public String ajoutPatientSubmit(
            @Valid @ModelAttribute("patient") PatientForm patient,
            BindingResult resultatDeLiaison,
            RedirectAttributes redirection) {
        if (resultatDeLiaison.hasErrors()) {
            return VUE_AJOUT;
        }
        patientApiService.creeLePatient(patient);
        redirection.addFlashAttribute(ConstantesToast.CLE_NIVEAU, ConstantesToast.NIVEAU_INFO);
        redirection.addFlashAttribute(ConstantesToast.CLE_MESSAGE, "Patient créé.");
        return "redirect:/app/dashboard";
    }
}
