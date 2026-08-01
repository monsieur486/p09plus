package com.mr486.msnotes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Vérifie que le contexte applicatif démarre et que les migrations s'appliquent.
 *
 * <p>Le test s'appuie sur une base MongoDB jetable plutôt que sur une base locale : il
 * s'exécute donc partout, et valide au passage que la migration Mongock d'amorçage se
 * déroule sans erreur.</p>
 */
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        // Le conteneur de test n'exige pas d'authentification, contrairement à la base de
        // développement : on neutralise les identifiants hérités de la configuration.
        "spring.data.mongodb.username=",
        "spring.data.mongodb.password=",
        "spring.data.mongodb.authentication-database="})
@DisplayName("Démarrage du microservice des notes")
class MsNotesApplicationTests {

    /** Base MongoDB jetable, démarrée le temps des tests. */
    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:8.0.14");

    @Test
    @DisplayName("le contexte se charge et la migration d'amorçage s'exécute")
    void contextLoads() {
        // Le seul démarrage du contexte suffit : Mongock exécute la migration d'amorçage
        // et échoue si elle est mal formée.
    }
}
