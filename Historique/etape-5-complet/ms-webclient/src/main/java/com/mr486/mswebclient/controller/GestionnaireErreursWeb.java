package com.mr486.mswebclient.controller;

import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.commun.exception.ServiceIndisponibleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Présente les erreurs de l'interface web sous une forme compréhensible par le praticien.
 *
 * <p>Comme pour les API, le traitement distingue les deux familles d'erreurs : une demande
 * portant sur une fiche inexistante (4xx) affiche un message précis invitant à corriger la
 * navigation, tandis qu'une panne (5xx) affiche un message rassurant sans détail technique,
 * la cause étant journalisée côté serveur. Dans tous les cas, l'utilisateur voit une page
 * du site plutôt que la page d'erreur par défaut du serveur.</p>
 *
 * <p><b>Exemple :</b> consulter un patient alors que le service des patients est arrêté
 * affiche « Le service ms-patients est momentanément indisponible. » et propose de
 * réessayer.</p>
 */
@ControllerAdvice
@Slf4j
public class GestionnaireErreursWeb {

    /** Nom de la vue affichant une erreur à l'utilisateur. */
    private static final String VUE_ERREUR = "erreur";

    /** Message affiché lorsqu'une erreur imprévue survient. */
    private static final String MESSAGE_ERREUR_INTERNE =
            "Une erreur inattendue est survenue. L'équipe technique a été informée.";

    /**
     * Affiche une page d'erreur lorsque la ressource demandée n'existe pas.
     *
     * <p><b>Exemple :</b> ouvrir la fiche d'un patient supprimé affiche « La ressource
     * demandée est introuvable. ».</p>
     *
     * @param ex    exception signalant l'absence de la ressource
     * @param model modèle transmis à la vue
     * @return le nom de la vue d'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceIntrouvable(ResourceNotFoundException ex, Model model) {
        log.warn("ressource demandée introuvable : {}", ex.getMessage());
        model.addAttribute("titre", "Page introuvable");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("reessayable", false);
        return VUE_ERREUR;
    }

    /**
     * Affiche une page d'erreur lorsqu'un microservice ne répond pas.
     *
     * <p><b>Exemple :</b> évaluer un risque alors que le service des notes est arrêté
     * affiche « Le service ms-notes est momentanément indisponible. » et invite à
     * réessayer.</p>
     *
     * @param ex    exception signalant le service injoignable
     * @param model modèle transmis à la vue
     * @return le nom de la vue d'erreur
     */
    @ExceptionHandler(ServiceIndisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String serviceIndisponible(ServiceIndisponibleException ex, Model model) {
        log.error("service {} injoignable depuis l'interface web", ex.getServiceAppele(), ex);
        model.addAttribute("titre", "Service momentanément indisponible");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("reessayable", true);
        return VUE_ERREUR;
    }

    /**
     * Affiche une page d'erreur lorsqu'une ressource statique demandée n'existe pas.
     *
     * <p>Sans ce traitement, l'absence d'un fichier remonterait au gestionnaire générique et
     * produirait un statut 500 : une simple URL erronée serait comptée comme une panne du
     * service et polluerait les journaux d'incidents.</p>
     *
     * <p><b>Exemple :</b> une requête sur {@code /favicon.ico} alors que l'icône est servie
     * en SVG retourne un statut 404, pas 500.</p>
     *
     * @param ex    exception signalant la ressource statique absente
     * @param model modèle transmis à la vue
     * @return le nom de la vue d'erreur
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceStatiqueIntrouvable(NoResourceFoundException ex, Model model) {
        log.warn("ressource statique absente : {}", ex.getResourcePath());
        model.addAttribute("titre", "Page introuvable");
        model.addAttribute("message", "La ressource demandée est introuvable.");
        model.addAttribute("reessayable", false);
        return VUE_ERREUR;
    }

    /**
     * Affiche une page d'erreur générique pour toute défaillance non anticipée.
     *
     * <p><b>Exemple :</b> une erreur de configuration affiche un message neutre, sans
     * exposer de trace technique au praticien.</p>
     *
     * @param ex    exception inattendue remontée jusqu'au contrôleur
     * @param model modèle transmis à la vue
     * @return le nom de la vue d'erreur
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String erreurInattendue(Exception ex, Model model) {
        log.error("erreur inattendue dans l'interface web", ex);
        model.addAttribute("titre", "Erreur inattendue");
        model.addAttribute("message", MESSAGE_ERREUR_INTERNE);
        model.addAttribute("reessayable", false);
        return VUE_ERREUR;
    }
}
