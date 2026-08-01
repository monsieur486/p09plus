package com.mr486.msrisque;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Vérifie que le contexte applicatif démarre.
 *
 * <p>Ce microservice n'a pas de base de données : le test contrôle que les clients Feign,
 * la sécurité et le gestionnaire d'erreurs importés de la bibliothèque commune s'assemblent
 * correctement.</p>
 */
@SpringBootTest
@DisplayName("Démarrage du microservice d'évaluation du risque")
class MsRisqueApplicationTests {

    @Test
    @DisplayName("le contexte se charge avec les clients Feign déclarés")
    void contextLoads() {
        // Le seul démarrage du contexte suffit : une dépendance manquante ou un bean mal
        // configuré ferait échouer son assemblage.
    }
}
