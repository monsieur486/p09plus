package com.mr486.mswebclient.service;

import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.commun.exception.ServiceIndisponibleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ClientPasserelle {

    private final RestTemplate restTemplate;
    private final String urlDeLaPasserelle;

    public ClientPasserelle(
            RestTemplate restTemplate,
            @Value("${app.gateway.base-url}") String urlDeLaPasserelle) {
        this.restTemplate = restTemplate;
        this.urlDeLaPasserelle = urlDeLaPasserelle;
    }

    public <T> T echange(
            HttpMethod methode,
            String chemin,
            Object corps,
            ParameterizedTypeReference<T> type,
            String service) {
        try {
            HttpEntity<Object> requete = corps == null ? null : new HttpEntity<>(corps);
            return restTemplate.exchange(urlDeLaPasserelle + chemin, methode, requete, type).getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("ressource absente sur {} : {} {}", service, methode, chemin);
            throw new ResourceNotFoundException("La ressource demandée est introuvable.");
        } catch (RestClientException ex) {
            throw new ServiceIndisponibleException(service, ex);
        }
    }
}
