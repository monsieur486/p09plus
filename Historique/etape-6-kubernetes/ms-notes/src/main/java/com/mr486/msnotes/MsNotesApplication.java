package com.mr486.msnotes;

import com.mr486.commun.configuration.DocumentationApiConfiguration;
import com.mr486.commun.configuration.SecuriteApiConfiguration;
import com.mr486.commun.exception.GlobalExceptionHandler;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Point d'entrée du microservice de gestion des notes.
 *
 * <p>La sécurité, la documentation OpenAPI et le traitement des erreurs sont importés
 * depuis la bibliothèque commune : ces configurations sont identiques d'un microservice à
 * l'autre, et l'import est explicite car elles vivent hors du paquetage balayé ici.</p>
 *
 * <p><b>Exemple :</b> le service démarre sur le port 9200 et applique au passage les
 * migrations Mongock en attente.</p>
 */
@SpringBootApplication
@EnableMongock
@Import({
        SecuriteApiConfiguration.class,
        DocumentationApiConfiguration.class,
        GlobalExceptionHandler.class})
public class MsNotesApplication {

    /**
     * Démarre le microservice.
     *
     * <p><b>Exemple :</b> {@code java -jar ms-notes.jar --spring.profiles.active=docker}
     * démarre le service avec la configuration destinée aux conteneurs.</p>
     *
     * @param args arguments de la ligne de commande transmis à Spring Boot
     */
    public static void main(final String[] args) {
        SpringApplication.run(MsNotesApplication.class, args);
    }
}
