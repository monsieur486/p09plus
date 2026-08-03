package com.mr486.commun.exception;

import com.mr486.commun.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Traduit les exceptions des microservices en réponses HTTP uniformes.
 *
 * <p>Le traitement distingue nettement deux familles d'erreurs :</p>
 *
 * <ul>
 *   <li><b>4xx — l'appelant est en cause</b> (donnée invalide, ressource inexistante,
 *       conflit). Le message métier lui est renvoyé tel quel pour qu'il puisse corriger
 *       sa demande, et l'incident est journalisé en {@code warn} : le service fonctionne
 *       normalement.</li>
 *   <li><b>5xx — le service est en cause</b> (panne, dépendance injoignable, bug). Seul un
 *       message générique est renvoyé, sans détail technique exploitable de l'extérieur, et
 *       la cause complète est journalisée en {@code error} avec sa pile d'appels.</li>
 * </ul>
 *
 * <p>Cette classe n'est pas détectée automatiquement par les microservices, qui ne
 * balayent que leur propre paquetage : chacun l'active par
 * {@code @Import(GlobalExceptionHandler.class)}.</p>
 *
 * <p><b>Exemple :</b> une demande portant sur un patient inexistant produit un statut 404
 * et le message « Aucun patient avec l'id: 42 » ; une panne de base de données produit un
 * statut 500 et le seul message « Une erreur interne est survenue. ».</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** Message renvoyé pour toute erreur serveur non anticipée, sans détail technique. */
    private static final String MESSAGE_ERREUR_INTERNE = "Une erreur interne est survenue.";

    /** Nom du microservice, repris dans chaque réponse pour situer l'erreur. */
    // Non final malgré creedengo : Spring affecte la valeur après avoir construit l'objet,
    // ce qu'un champ final rendrait impossible.
    @SuppressWarnings("creedengo-java:GCI82")
    @Value("${spring.application.name:application}")
    private String nomDuMicroservice;

    /**
     * Traduit une ressource métier introuvable en réponse 404.
     *
     * <p><b>Exemple :</b> une demande portant sur un patient inexistant retourne un statut
     * 404 et le message « Aucun patient avec l'id: 42 ».</p>
     *
     * @param ex      exception signalant l'absence de la ressource
     * @param requete requête à l'origine de l'erreur
     * @return la réponse 404 normalisée
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(
            final ResourceNotFoundException ex, final HttpServletRequest requete) {
        return erreurClient(HttpStatus.NOT_FOUND, List.of(ex.getMessage()), requete);
    }

    /**
     * Traduit une violation d'unicité métier en réponse 409.
     *
     * <p>Le conflit porte sur l'état de la ressource, pas sur la syntaxe de la demande :
     * le statut 409 le signale plus précisément qu'un 400.</p>
     *
     * <p><b>Exemple :</b> enregistrer deux fois le même patient retourne un statut 409 et
     * le message « Le patient existe déjà dans la base de données. ».</p>
     *
     * @param ex      exception signalant le doublon
     * @param requete requête à l'origine de l'erreur
     * @return la réponse 409 normalisée
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Object> handleDuplication(
            final DuplicateException ex, final HttpServletRequest requete) {
        return erreurClient(HttpStatus.CONFLICT, List.of(ex.getMessage()), requete);
    }

    /**
     * Traduit les violations de contraintes portées par les paramètres d'URL en réponse 400.
     *
     * <p><b>Exemple :</b> un identifiant négatif retourne un statut 400 et le message
     * « getPatient.id: doit être supérieur à 0 ».</p>
     *
     * @param ex      exception portant les violations détectées
     * @param requete requête à l'origine de l'erreur
     * @return la réponse 400 normalisée
     */
    @ExceptionHandler(ConstraintViolationException.class)
    // creedengo vise ici le paramètre de la lambda passée à map. Le rendre final obligerait
    // à en écrire le type, que le compilateur déduit du flux, pour un gain nul.
    @SuppressWarnings("creedengo-java:GCI82")
    public ResponseEntity<Object> handleConstraintViolation(
            final ConstraintViolationException ex, final HttpServletRequest requete) {
        final List<String> messages = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return erreurClient(HttpStatus.BAD_REQUEST, messages, requete);
    }

    /**
     * Traduit l'indisponibilité d'un microservice appelé en réponse 503.
     *
     * <p>L'appelant reçoit un message explicite — la demande pourra aboutir plus tard —
     * mais aucun détail technique sur la panne.</p>
     *
     * <p><b>Exemple :</b> évaluer le risque d'un patient alors que le service des notes est
     * arrêté retourne un statut 503 et le message « Le service ms-notes est momentanément
     * indisponible. ».</p>
     *
     * @param ex      exception signalant le service injoignable
     * @param requete requête à l'origine de l'erreur
     * @return la réponse 503 normalisée
     */
    @ExceptionHandler(ServiceIndisponibleException.class)
    public ResponseEntity<Object> handleServiceIndisponible(
            final ServiceIndisponibleException ex, final HttpServletRequest requete) {
        return erreurServeur(
                HttpStatus.SERVICE_UNAVAILABLE, List.of(ex.getMessage()), requete, ex);
    }

    /**
     * Traduit une erreur non anticipée en réponse 500, sans divulguer de détail technique.
     *
     * <p><b>Exemple :</b> une panne de base de données retourne un statut 500 et le seul
     * message « Une erreur interne est survenue. » ; la cause reste consultable dans les
     * journaux du service.</p>
     *
     * @param ex      exception inattendue remontée jusqu'au contrôleur
     * @param requete requête à l'origine de l'erreur
     * @return la réponse 500 normalisée
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(final Exception ex, final HttpServletRequest requete) {
        return erreurServeur(
                HttpStatus.INTERNAL_SERVER_ERROR, List.of(MESSAGE_ERREUR_INTERNE), requete, ex);
    }

    /** Traduit les erreurs de validation d'un corps de requête annoté {@code @Valid} en 400. */
    @Override
    // GCI82 vise le paramètre de la lambda passée à forEach : le rendre final obligerait à
    // en écrire le type, que le compilateur déduit du flux, pour un gain nul.
    // S2638 réclame un type de retour @Nullable, pour honorer le contrat de la méthode
    // héritée de Spring. Ce serait mentir sur ce code : il renvoie toujours une réponse.
    @SuppressWarnings({"creedengo-java:GCI82", "java:S2638"})
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex, final HttpHeaders enTetes,
            final HttpStatusCode statut, final WebRequest webRequest) {

        final List<String> messages = new ArrayList<>();
        for (final FieldError erreurDeChamp : ex.getBindingResult().getFieldErrors()) {
            messages.add(erreurDeChamp.getField() + ": " + erreurDeChamp.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(err -> messages.add(err.getObjectName() + ": " + err.getDefaultMessage()));

        return erreurClient(HttpStatus.BAD_REQUEST, messages, requeteDe(webRequest));
    }

    /** Traduit l'absence de route correspondant à l'URL appelée en 404. */
    @Override
    // S2638 réclame un type de retour @Nullable, pour honorer le contrat de la méthode
    // héritée de Spring. Ce serait mentir sur ce code : il renvoie toujours une réponse.
    @SuppressWarnings("java:S2638")
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            final NoHandlerFoundException ex, final HttpHeaders enTetes, final HttpStatusCode statut,
            final WebRequest webRequest) {

        final String message = "Route non trouvée: " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return erreurClient(HttpStatus.NOT_FOUND, List.of(message), requeteDe(webRequest));
    }

    // Erreur imputable à l'appelant : le message métier lui est utile, l'incident reste bénin.
    private ResponseEntity<Object> erreurClient(
            final HttpStatus statut, final List<String> messages, final HttpServletRequest requete) {
        log.warn("requête refusée ({}) sur {} : {}", statut.value(), requete.getRequestURI(), messages);
        return reponse(statut, messages, requete);
    }

    // Erreur imputable au service : l'appelant n'obtient qu'un message générique, la cause est tracée.
    private ResponseEntity<Object> erreurServeur(
            final HttpStatus statut, final List<String> messages,
            final HttpServletRequest requete, final Exception ex) {
        log.error("échec du traitement ({}) sur {}", statut.value(), requete.getRequestURI(), ex);
        return reponse(statut, messages, requete);
    }

    // Assemble le corps normalisé renvoyé au client.
    private ResponseEntity<Object> reponse(
            final HttpStatus statut, final List<String> messages, final HttpServletRequest requete) {
        final ErrorResponse corps = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .path(requete.getRequestURI())
                .errorCode(statut.name())
                .microserviceName(nomDuMicroservice)
                .messages(messages)
                .build();
        return ResponseEntity.status(statut).body(corps);
    }

    // Extrait la requête servlet des handlers hérités, qui reçoivent un WebRequest.
    private HttpServletRequest requeteDe(final WebRequest webRequest) {
        return (HttpServletRequest) webRequest.resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }
}
