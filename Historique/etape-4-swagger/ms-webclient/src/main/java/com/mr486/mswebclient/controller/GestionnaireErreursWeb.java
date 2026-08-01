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

@ControllerAdvice
@Slf4j
public class GestionnaireErreursWeb {

    private static final String VUE_ERREUR = "erreur";

    private static final String MESSAGE_ERREUR_INTERNE =
            "Une erreur inattendue est survenue. L'équipe technique a été informée.";

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceIntrouvable(ResourceNotFoundException ex, Model model) {
        log.warn("ressource demandée introuvable : {}", ex.getMessage());
        model.addAttribute("titre", "Page introuvable");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("reessayable", false);
        return VUE_ERREUR;
    }

    @ExceptionHandler(ServiceIndisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String serviceIndisponible(ServiceIndisponibleException ex, Model model) {
        log.error("service {} injoignable depuis l'interface web", ex.getServiceAppele(), ex);
        model.addAttribute("titre", "Service momentanément indisponible");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("reessayable", true);
        return VUE_ERREUR;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceStatiqueIntrouvable(NoResourceFoundException ex, Model model) {
        log.warn("ressource statique absente : {}", ex.getResourcePath());
        model.addAttribute("titre", "Page introuvable");
        model.addAttribute("message", "La ressource demandée est introuvable.");
        model.addAttribute("reessayable", false);
        return VUE_ERREUR;
    }

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
