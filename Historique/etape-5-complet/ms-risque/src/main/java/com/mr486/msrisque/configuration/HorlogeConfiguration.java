package com.mr486.msrisque.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expose l'horloge du système sous forme de bean injectable.
 *
 * <p>Le calcul de l'âge d'un patient dépend de la date courante. En passant par une
 * {@link Clock} injectée plutôt que par {@code LocalDate.now()}, les services restent
 * testables avec une date figée.</p>
 *
 * <p><b>Exemple :</b> en production le bean retourne l'heure réelle du serveur ; un test
 * peut lui substituer {@code Clock.fixed(Instant.parse("2026-08-01T00:00:00Z"), ZoneOffset.UTC)}
 * pour vérifier le comportement à une date précise.</p>
 */
@Configuration
public class HorlogeConfiguration {

    /**
     * Fournit l'horloge utilisée par l'application pour dater ses traitements.
     *
     * <p><b>Exemple :</b> {@code LocalDate.now(horloge)} renvoie la date du jour dans le
     * fuseau horaire par défaut du serveur.</p>
     *
     * @return l'horloge du système, dans le fuseau horaire par défaut
     */
    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }
}
