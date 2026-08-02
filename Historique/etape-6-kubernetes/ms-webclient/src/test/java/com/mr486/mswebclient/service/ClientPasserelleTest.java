package com.mr486.mswebclient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mr486.commun.exception.ResourceNotFoundException;
import com.mr486.commun.exception.ServiceIndisponibleException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Tests de la traduction des échecs d'appel en exceptions métier.
 *
 * <p>C'est la seule décision portée par l'interface web : distinguer une ressource absente,
 * imputable à la demande de l'utilisateur, d'un service injoignable, imputable à
 * l'infrastructure. Les confondre afficherait « page introuvable » lors d'une panne, et
 * masquerait l'incident.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Appels aux microservices via la passerelle")
class ClientPasserelleTest {

    /** Adresse de la passerelle utilisée par les scénarios. */
    private static final String URL_PASSERELLE = "http://localhost:9000";

    /** Chemin appelé par les scénarios. */
    private static final String CHEMIN = "/ms-patients/patients";

    /** Nom du microservice appelé, repris dans les messages d'indisponibilité. */
    private static final String SERVICE = "ms-patients";

    /** Type de retour attendu par les scénarios. */
    private static final ParameterizedTypeReference<String> TYPE_ATTENDU =
            new ParameterizedTypeReference<>() {};

    @Mock
    private RestTemplate restTemplate;

    private ClientPasserelle clientPasserelle;

    @BeforeEach
    void initialiseLeClient() {
        clientPasserelle = new ClientPasserelle(restTemplate, URL_PASSERELLE);
    }

    @Test
    @DisplayName("un appel abouti retourne le corps de la réponse")
    void appelAbouti_retourneLeCorps() {
        when(restTemplate.exchange(
                eq(URL_PASSERELLE + CHEMIN), eq(HttpMethod.GET),
                any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok("contenu"));

        String resultat = clientPasserelle.echange(
                HttpMethod.GET, CHEMIN, null, TYPE_ATTENDU, SERVICE);

        assertThat(resultat).isEqualTo("contenu");
    }

    @Test
    @DisplayName("le corps transmis est encapsulé dans la requête envoyée")
    void appelAvecCorps_transmetLeCorps() {
        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok("créé"));

        clientPasserelle.echange(HttpMethod.POST, CHEMIN, "charge utile", TYPE_ATTENDU, SERVICE);

        // C'est la requête réellement envoyée qu'il faut inspecter : se contenter du corps
        // de la réponse laisserait passer un envoi sans charge utile.
        ArgumentCaptor<HttpEntity<Object>> requeteEnvoyee = ArgumentCaptor.captor();
        verify(restTemplate).exchange(
                anyString(), eq(HttpMethod.POST),
                requeteEnvoyee.capture(), any(ParameterizedTypeReference.class));
        assertThat(requeteEnvoyee.getValue().getBody()).isEqualTo("charge utile");
    }

    @Test
    @DisplayName("une réponse 404 devient une ressource introuvable, pas une panne")
    void reponse404_leveResourceNotFound() {
        when(restTemplate.exchange(
                anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY,
                        new byte[0], StandardCharsets.UTF_8));

        assertThatThrownBy(() -> clientPasserelle.echange(
                HttpMethod.GET, CHEMIN, null, TYPE_ATTENDU, SERVICE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("un service injoignable devient une indisponibilité nommée")
    void serviceInjoignable_leveServiceIndisponible() {
        when(restTemplate.exchange(
                anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("connexion refusée"));

        assertThatThrownBy(() -> clientPasserelle.echange(
                HttpMethod.GET, CHEMIN, null, TYPE_ATTENDU, SERVICE))
                .isInstanceOf(ServiceIndisponibleException.class)
                .hasMessageContaining(SERVICE);
    }

    @Test
    @DisplayName("une erreur 4xx autre que 404 devient elle aussi une indisponibilité")
    void erreur4xxAutreQue404_leveServiceIndisponible() {
        when(restTemplate.exchange(
                anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY,
                        new byte[0], StandardCharsets.UTF_8));

        assertThatThrownBy(() -> clientPasserelle.echange(
                HttpMethod.GET, CHEMIN, null, TYPE_ATTENDU, SERVICE))
                .isInstanceOf(ServiceIndisponibleException.class);
    }
}
