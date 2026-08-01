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

/**
 * Effectue les appels de l'interface web vers les microservices, au travers de la passerelle.
 *
 * <p>Ce composant centralise deux préoccupations que les contrôleurs n'ont pas à porter :
 * la construction de l'URL complète, et la traduction des échecs réseau en exceptions
 * métier. Une passerelle ou un microservice arrêté devient ainsi une
 * {@link ServiceIndisponibleException}, présentée à l'utilisateur comme une panne
 * temporaire plutôt que comme une trace technique.</p>
 *
 * <p><b>Exemple :</b> {@code echange(HttpMethod.GET, "/ms-patients/patients", null, type,
 * "ms-patients")} retourne la liste des patients, ou lève une
 * {@link ServiceIndisponibleException} si le service ne répond pas.</p>
 */
@Component
@Slf4j
public class ClientPasserelle {

    private final RestTemplate restTemplate;
    private final String urlDeLaPasserelle;

    /**
     * Construit le client à partir du modèle d'appel authentifié et de l'adresse de la passerelle.
     *
     * <p><b>Exemple :</b> en configuration Docker, {@code urlDeLaPasserelle} vaut
     * {@code http://ms-gateway:9000}.</p>
     *
     * @param restTemplate      modèle d'appel HTTP, déjà porteur de l'authentification
     * @param urlDeLaPasserelle adresse de base de la passerelle
     */
    public ClientPasserelle(
            RestTemplate restTemplate,
            @Value("${app.gateway.base-url}") String urlDeLaPasserelle) {
        this.restTemplate = restTemplate;
        this.urlDeLaPasserelle = urlDeLaPasserelle;
    }

    /**
     * Exécute un appel vers un microservice et retourne le corps de la réponse.
     *
     * <p><b>Exemple :</b> un appel sur un patient inexistant lève une
     * {@link ResourceNotFoundException} ; un service arrêté lève une
     * {@link ServiceIndisponibleException}.</p>
     *
     * @param methode  méthode HTTP à employer
     * @param chemin   chemin de la ressource derrière la passerelle
     * @param corps    corps de la requête, ou {@code null} pour une lecture
     * @param type     type attendu de la réponse
     * @param service  nom du microservice appelé, repris dans le message d'erreur
     * @param <T>      type du corps de la réponse
     * @return le corps de la réponse, éventuellement {@code null} si le service n'en renvoie pas
     * @throws ResourceNotFoundException    si le microservice répond que la ressource n'existe pas
     * @throws ServiceIndisponibleException si le microservice est injoignable ou en erreur
     */
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
