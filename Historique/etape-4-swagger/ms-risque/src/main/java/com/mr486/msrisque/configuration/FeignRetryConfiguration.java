package com.mr486.msrisque.configuration;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfiguration {

    private static final long ATTENTE_INITIALE_MS = 100L;

    private static final long ATTENTE_MAXIMALE_MS = 1000L;

    private static final int NOMBRE_DE_TENTATIVES = 3;

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(ATTENTE_INITIALE_MS, ATTENTE_MAXIMALE_MS, NOMBRE_DE_TENTATIVES);
    }
}
