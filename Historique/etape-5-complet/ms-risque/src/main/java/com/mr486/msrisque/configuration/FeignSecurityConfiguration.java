package com.mr486.msrisque.configuration;

import feign.RequestInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Authentifie les appels sortants vers les autres microservices.
 *
 * <p>Les API appelées exigent une authentification HTTP Basic. Cet intercepteur ajoute
 * l'en-tête correspondant à chaque requête émise par les clients Feign, à partir du compte
 * de service configuré : aucun identifiant n'est écrit dans le code.</p>
 *
 * <p><b>Exemple :</b> un appel à {@code GET /patients/1} part avec un en-tête
 * {@code Authorization: Basic …} construit depuis {@code security.app-user.*}.</p>
 */
@Configuration
public class FeignSecurityConfiguration {

    /** Identifiant du compte de service, injecté depuis la configuration. */
    @Value("${security.app-user.username}")
    private String utilisateur;

    /** Mot de passe du compte de service, injecté depuis la configuration. */
    @Value("${security.app-user.password}")
    private String motDePasse;

    /**
     * Fournit l'intercepteur qui authentifie chaque appel sortant.
     *
     * <p><b>Exemple :</b> tout appel émis par un client Feign porte désormais l'en-tête
     * d'authentification attendu par le service appelé.</p>
     *
     * @return l'intercepteur ajoutant l'en-tête d'authentification
     */
    @Bean
    public RequestInterceptor intercepteurAuthentification() {
        return gabarit -> {
            String identifiants = utilisateur + ":" + motDePasse;
            String encode = Base64.getEncoder()
                    .encodeToString(identifiants.getBytes(StandardCharsets.UTF_8));
            gabarit.header("Authorization", "Basic " + encode);
        };
    }
}
