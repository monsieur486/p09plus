package com.mr486.mswebclient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Vérifie que le contexte applicatif démarre.
 *
 * <p>Cette application n'a ni base de données ni dépendance appelée au démarrage : le test
 * contrôle que la sécurité, les contrôleurs et les services d'appel s'assemblent
 * correctement.</p>
 */
@SpringBootTest
@DisplayName("Démarrage de l'interface web")
class MsWebclientApplicationTests {

    @Test
    @DisplayName("le contexte se charge avec la configuration de sécurité")
    void contextLoads() {
        // Le seul démarrage du contexte suffit : un bean mal configuré ferait échouer son
        // assemblage.
    }
}
