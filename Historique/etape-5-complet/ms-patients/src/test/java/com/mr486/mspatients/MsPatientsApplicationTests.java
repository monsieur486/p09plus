package com.mr486.mspatients;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Vérifie que le contexte applicatif démarre et que les migrations s'appliquent.
 *
 * <p>Le test s'appuie sur une base PostgreSQL jetable plutôt que sur une base locale : il
 * s'exécute donc partout, et valide au passage que les changelogs Liquibase produisent le
 * schéma attendu par les entités, dont Hibernate contrôle la conformité au démarrage.</p>
 */
@SpringBootTest
@Testcontainers
@DisplayName("Démarrage du microservice des patients")
class MsPatientsApplicationTests {

    /** Base PostgreSQL jetable, démarrée le temps des tests. */
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Test
    @DisplayName("le contexte se charge et le schéma est conforme aux entités")
    void contextLoads() {
        // Le seul démarrage du contexte suffit : Liquibase applique les changelogs et
        // Hibernate échoue si le schéma obtenu ne correspond pas aux entités.
    }
}
