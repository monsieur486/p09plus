package com.mr486.mswebclient.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Prépare le client HTTP utilisé pour appeler les microservices via la passerelle.
 *
 * <p>Les API internes exigent une authentification HTTP Basic. Elle est posée une fois
 * pour toutes sur le client, afin qu'aucun appelant n'ait à s'en préoccuper ni à
 * manipuler les identifiants.</p>
 *
 * <p><b>Exemple :</b> un appel émis vers {@code /ms-patients/patients} part avec l'en-tête
 * d'authentification du compte de service.</p>
 */
@Configuration
public class GatewaySecurityConfiguration {

    /**
     * Fournit le client HTTP authentifié auprès des microservices.
     *
     * <p><b>Exemple :</b> avec les valeurs par défaut, les appels sont émis au nom du
     * compte {@code app_user}.</p>
     *
     * @param utilisateur identifiant du compte de service
     * @param motDePasse  mot de passe du compte de service
     * @return le client HTTP prêt à appeler la passerelle
     */
    @Bean
    public RestTemplate restTemplate(
            @Value("${app.auth.username}") String utilisateur,
            @Value("${app.auth.password}") String motDePasse) {
        RestTemplate client = new RestTemplate();
        client.getInterceptors().add(new BasicAuthenticationInterceptor(utilisateur, motDePasse));
        return client;
    }
}
