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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MESSAGE_ERREUR_INTERNE = "Une erreur interne est survenue.";

    @Value("${spring.application.name:application}")
    private String nomDuMicroservice;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest requete) {
        return erreurClient(HttpStatus.NOT_FOUND, List.of(ex.getMessage()), requete);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Object> handleDuplication(
            DuplicateException ex, HttpServletRequest requete) {
        return erreurClient(HttpStatus.CONFLICT, List.of(ex.getMessage()), requete);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest requete) {
        List<String> messages = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return erreurClient(HttpStatus.BAD_REQUEST, messages, requete);
    }

    @ExceptionHandler(ServiceIndisponibleException.class)
    public ResponseEntity<Object> handleServiceIndisponible(
            ServiceIndisponibleException ex, HttpServletRequest requete) {
        return erreurServeur(
                HttpStatus.SERVICE_UNAVAILABLE, List.of(ex.getMessage()), requete, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest requete) {
        return erreurServeur(
                HttpStatus.INTERNAL_SERVER_ERROR, List.of(MESSAGE_ERREUR_INTERNE), requete, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders enTetes,
            HttpStatusCode statut, WebRequest webRequest) {

        List<String> messages = new ArrayList<>();
        for (FieldError erreurDeChamp : ex.getBindingResult().getFieldErrors()) {
            messages.add(erreurDeChamp.getField() + ": " + erreurDeChamp.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(err -> messages.add(err.getObjectName() + ": " + err.getDefaultMessage()));

        return erreurClient(HttpStatus.BAD_REQUEST, messages, requeteDe(webRequest));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders enTetes, HttpStatusCode statut,
            WebRequest webRequest) {

        String message = "Route non trouvée: " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return erreurClient(HttpStatus.NOT_FOUND, List.of(message), requeteDe(webRequest));
    }

    private ResponseEntity<Object> erreurClient(
            HttpStatus statut, List<String> messages, HttpServletRequest requete) {
        log.warn("requête refusée ({}) sur {} : {}", statut.value(), requete.getRequestURI(), messages);
        return reponse(statut, messages, requete);
    }

    private ResponseEntity<Object> erreurServeur(
            HttpStatus statut, List<String> messages, HttpServletRequest requete, Exception ex) {
        log.error("échec du traitement ({}) sur {}", statut.value(), requete.getRequestURI(), ex);
        return reponse(statut, messages, requete);
    }

    private ResponseEntity<Object> reponse(
            HttpStatus statut, List<String> messages, HttpServletRequest requete) {
        ErrorResponse corps = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .path(requete.getRequestURI())
                .errorCode(statut.name())
                .microserviceName(nomDuMicroservice)
                .messages(messages)
                .build();
        return ResponseEntity.status(statut).body(corps);
    }

    private HttpServletRequest requeteDe(WebRequest webRequest) {
        return (HttpServletRequest) webRequest.resolveReference(WebRequest.REFERENCE_REQUEST);
    }
}
