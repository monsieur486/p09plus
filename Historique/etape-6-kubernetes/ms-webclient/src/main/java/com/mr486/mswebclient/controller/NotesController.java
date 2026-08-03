package com.mr486.mswebclient.controller;

import com.mr486.commun.dto.NoteDto;
import com.mr486.mswebclient.configuration.ConstantesToast;
import com.mr486.mswebclient.service.NoteApiService;
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
 * Gère l'ajout d'une note à un patient.
 *
 * <p>La consultation des notes n'a pas de page propre : elle est intégrée à la fiche du
 * patient, aux côtés du niveau de risque.</p>
 *
 * <p><b>Exemple :</b> {@code POST /app/dashboard/2/notes} enregistre une note puis redirige
 * vers la fiche du patient 2.</p>
 */
@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class NotesController {

    /** Vue du formulaire d'ajout, réaffichée telle quelle en cas de saisie invalide. */
    private static final String VUE_AJOUT = "note-ajout";

    /** Attribut portant l'identifiant du patient dans les vues. */
    private static final String ATTRIBUT_ID = "patientId";

    private final NoteApiService noteApiService;

    /**
     * Affiche le formulaire d'ajout d'une note.
     *
     * <p><b>Exemple :</b> {@code GET /app/dashboard/2/notes/ajout} affiche une zone de
     * saisie vierge.</p>
     *
     * @param patientId identifiant du patient concerné
     * @param model     modèle transmis à la vue
     * @return le nom de la vue du formulaire
     */
    @GetMapping("/dashboard/{patientId}/notes/ajout")
    public String showCreateNoteForm(final @PathVariable Long patientId, final Model model) {
        model.addAttribute("note", new NoteDto());
        model.addAttribute(ATTRIBUT_ID, patientId);
        return VUE_AJOUT;
    }

    /**
     * Enregistre une nouvelle note pour un patient.
     *
     * <p><b>Exemple :</b> une note vide réaffiche le formulaire avec le message d'erreur ;
     * une note valide redirige vers la fiche du patient.</p>
     *
     * @param patientId         identifiant du patient concerné
     * @param note              contenu saisi dans le formulaire
     * @param resultatDeLiaison résultat de la validation du formulaire
     * @param model             modèle transmis à la vue
     * @param redirection       porteur du message affiché après la redirection
     * @return la redirection vers la fiche du patient, ou le formulaire en cas d'erreur
     */
    @PostMapping("/dashboard/{patientId}/notes")
    public String ajoutNoteSubmit(
            final @PathVariable Long patientId,
            final @Valid @ModelAttribute("note") NoteDto note,
            final BindingResult resultatDeLiaison,
            final Model model,
            final RedirectAttributes redirection) {
        if (resultatDeLiaison.hasErrors()) {
            model.addAttribute(ATTRIBUT_ID, patientId);
            return VUE_AJOUT;
        }
        noteApiService.ajouteUneNote(patientId, note);
        redirection.addFlashAttribute(ConstantesToast.CLE_NIVEAU, ConstantesToast.NIVEAU_INFO);
        redirection.addFlashAttribute(ConstantesToast.CLE_MESSAGE, "Note ajoutée au dossier.");
        return "redirect:/app/dashboard/" + patientId;
    }
}
