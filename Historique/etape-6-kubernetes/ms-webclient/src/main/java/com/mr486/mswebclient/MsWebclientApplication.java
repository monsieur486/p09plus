package com.mr486.mswebclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'interface web destinée aux praticiens.
 *
 * <p>Cette application ne possède ni base de données ni API publique : elle affiche des
 * pages Thymeleaf alimentées par les microservices, appelés au travers de la passerelle.
 * Elle ne s'enregistre donc pas auprès du registre de services.</p>
 *
 * <p><b>Exemple :</b> l'application démarre sur le port 8080 et présente l'écran de
 * connexion sur {@code http://localhost:8080}.</p>
 */
@SpringBootApplication
public class MsWebclientApplication {

    /**
     * Démarre l'interface web.
     *
     * <p><b>Exemple :</b> {@code java -jar ms-webclient.jar --spring.profiles.active=docker}
     * démarre l'application avec la configuration destinée aux conteneurs.</p>
     *
     * @param args arguments de la ligne de commande transmis à Spring Boot
     */
    public static void main(final String[] args) {
        SpringApplication.run(MsWebclientApplication.class, args);
    }
}
