package com.mr486.msrisque;

import com.mr486.commun.configuration.DocumentationApiConfiguration;
import com.mr486.commun.configuration.SecuriteApiConfiguration;
import com.mr486.commun.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * Point d'entrée du microservice d'évaluation du risque de diabète.
 *
 * <p>Ce service ne possède pas de base de données : il interroge les microservices des
 * patients et des notes au moyen de clients Feign, résolus par le registre de services.
 * La sécurité, la documentation OpenAPI et le traitement des erreurs proviennent de la
 * bibliothèque commune et sont importés explicitement.</p>
 *
 * <p><b>Exemple :</b> le service démarre sur le port 9300 et expose
 * {@code GET /evaluation/{patientId}}.</p>
 */
@SpringBootApplication
@EnableFeignClients
@Import({
        SecuriteApiConfiguration.class,
        DocumentationApiConfiguration.class,
        GlobalExceptionHandler.class})
public class MsRisqueApplication {

    /**
     * Démarre le microservice.
     *
     * <p><b>Exemple :</b> {@code java -jar ms-risque.jar --spring.profiles.active=docker}
     * démarre le service avec la configuration destinée aux conteneurs.</p>
     *
     * @param args arguments de la ligne de commande transmis à Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(MsRisqueApplication.class, args);
    }
}
