package com.mr486.msrisque.configuration;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Règle les nouvelles tentatives des appels sortants vers les autres microservices.
 *
 * <p>Les services étant répliqués, une instance peut disparaître au moment précis d'un
 * appel. Réessayer permet alors d'atteindre une autre instance sans que l'utilisateur ne
 * voie d'erreur. Le nombre de tentatives reste borné : au-delà, mieux vaut signaler une
 * indisponibilité que faire patienter indéfiniment.</p>
 *
 * <p><b>Exemple :</b> un appel qui échoue est retenté au plus deux fois, après une attente
 * croissante de 100 ms puis au maximum 1 seconde.</p>
 */
@Configuration
public class FeignRetryConfiguration {

    /** Attente avant la première nouvelle tentative, en millisecondes. */
    private static final long ATTENTE_INITIALE_MS = 100L;

    /** Attente maximale entre deux tentatives, en millisecondes. */
    private static final long ATTENTE_MAXIMALE_MS = 1000L;

    /** Nombre total de tentatives, la première comprise. */
    private static final int NOMBRE_DE_TENTATIVES = 3;

    /**
     * Fournit la politique de nouvelle tentative appliquée aux clients Feign.
     *
     * <p><b>Exemple :</b> si le service des notes ne répond pas, l'appel est retenté
     * jusqu'à trois fois au total avant de lever une erreur.</p>
     *
     * @return la politique de nouvelle tentative
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(ATTENTE_INITIALE_MS, ATTENTE_MAXIMALE_MS, NOMBRE_DE_TENTATIVES);
    }
}
