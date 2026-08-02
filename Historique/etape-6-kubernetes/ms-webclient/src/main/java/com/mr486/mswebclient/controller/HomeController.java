package com.mr486.mswebclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Affiche les pages ouvertes à tous : accueil et formulaire de connexion.
 *
 * <p><b>Exemple :</b> {@code GET /} affiche la page d'accueil sans exiger de connexion,
 * alors que {@code /app/dashboard} redirige vers {@code /login}.</p>
 */
@Controller
public class HomeController {

    /**
     * Affiche la page d'accueil.
     *
     * <p><b>Exemple :</b> {@code GET /} et {@code GET /home} affichent la même page.</p>
     *
     * @return le nom de la vue d'accueil
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    /**
     * Affiche le formulaire de connexion.
     *
     * <p><b>Exemple :</b> {@code GET /login} affiche le formulaire ; la soumission est
     * traitée par Spring Security, pas par ce contrôleur.</p>
     *
     * @return le nom de la vue de connexion
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
