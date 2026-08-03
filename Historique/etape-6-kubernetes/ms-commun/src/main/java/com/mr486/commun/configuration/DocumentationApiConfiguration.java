package com.mr486.commun.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publie le contrat OpenAPI des microservices derrière la passerelle.
 *
 * <p>La passerelle route les appels d'après le nom du microservice, placé en tête de
 * l'URL. Le serveur déclaré dans le contrat reprend donc ce nom, sans quoi le bouton
 * « Try it out » de l'interface Swagger appellerait le service en court-circuitant la
 * passerelle. Ce nom est lu depuis la configuration plutôt qu'écrit en dur, ce qui rend
 * cette classe utilisable telle quelle par chaque microservice.</p>
 *
 * <p><b>Exemple :</b> dans {@code ms-patients}, le contrat déclare le serveur
 * {@code /ms-patients}, et l'appel d'essai vise
 * {@code http://localhost:9000/ms-patients/patients}.</p>
 */
@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "basicAuth",
        scheme = "basic")
public class DocumentationApiConfiguration {

    /** Nom du microservice, qui sert aussi de préfixe de routage à la passerelle. */
    // Non final malgré creedengo : Spring affecte la valeur après avoir construit l'objet,
    // ce qu'un champ final rendrait impossible.
    @SuppressWarnings("creedengo-java:GCI82")
    @Value("${spring.application.name:application}")
    private String nomDuMicroservice;

    /**
     * Décrit le serveur à utiliser pour les appels d'essai de l'interface Swagger.
     *
     * <p><b>Exemple :</b> pour le service des notes, le contrat expose le serveur
     * {@code /ms-notes}.</p>
     *
     * @return le contrat OpenAPI, complété du serveur passant par la passerelle
     */
    @Bean
    public OpenAPI contratOpenApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/" + nomDuMicroservice)));
    }
}
