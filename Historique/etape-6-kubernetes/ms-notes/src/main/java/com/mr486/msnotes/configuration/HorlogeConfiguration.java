package com.mr486.msnotes.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expose l'horloge du système sous forme de bean injectable.
 *
 * <p>La date de création d'une note détermine l'ordre dans lequel le dossier est restitué.
 * En passant par une {@link Clock} injectée plutôt que par {@code Instant.now()}, cette
 * datation reste maîtrisable — un test peut figer l'heure, et le microservice date ses
 * traitements de la même façon que celui de l'évaluation du risque.</p>
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
     * <p><b>Exemple :</b> {@code Instant.now(horloge)} renvoie l'instant courant du serveur.</p>
     *
     * @return l'horloge du système, dans le fuseau horaire par défaut
     */
    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }
}
