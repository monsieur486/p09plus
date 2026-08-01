package com.mr486.msrisque.configuration;

import feign.RequestInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignSecurityConfiguration {
    @Value("${security.app-user.username}")
    private String utilisateur;

    @Value("${security.app-user.password}")
    private String motDePasse;

    @Bean
    public RequestInterceptor intercepteurAuthentification() {
        return gabarit -> {
            String identifiants = utilisateur + ":" + motDePasse;
            String encode = Base64.getEncoder()
                    .encodeToString(identifiants.getBytes(StandardCharsets.UTF_8));
            gabarit.header("Authorization", "Basic " + encode);
        };
    }
}
