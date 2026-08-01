package com.mr486.mspatients.configuration;

import com.mr486.mspatients.dto.ErrorResponse;
import com.mr486.mspatients.exeption.DuplicateException;
import com.mr486.mspatients.exeption.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application.
 * Provides methods to handle specific and generic exceptions,
 * returning standardized HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${spring.application.name:application}")
  private String appName;

  /* ---------- Outils ---------- */

  private ResponseEntity<Object> build(HttpStatus status, List<String> messages, HttpServletRequest req) {
    ErrorResponse body = ErrorResponse.builder()
            .timestamp(Instant.now().toString())
            .path(req.getRequestURI())
            .errorCode(status.name())
            .microserviceName(appName)
            .messages(messages)
            .build();
    return ResponseEntity.status(status).body(body);
  }

  /* ---------- 400: erreurs de validation @Valid (DTO @RequestBody) ---------- */

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, HttpHeaders headers,
          HttpStatusCode status, WebRequest webRequest) {

    List<String> messages = new ArrayList<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      messages.add(fe.getField() + ": " + fe.getDefaultMessage());
    }
    ex.getBindingResult().getGlobalErrors()
            .forEach(err -> messages.add(err.getObjectName() + ": " + err.getDefaultMessage()));

    HttpServletRequest req = (HttpServletRequest) webRequest.resolveReference(org.springframework.web.context.request.WebRequest.REFERENCE_REQUEST);
    return build(HttpStatus.BAD_REQUEST, messages, req);
  }

  /* ---------- 400: erreurs de validation sur paramètres @PathVariable/@RequestParam ---------- */

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
    List<String> messages = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();
    return build(HttpStatus.BAD_REQUEST, messages, req);
  }

  /* ---------- 404: ressource métier introuvable ---------- */

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, List.of(ex.getMessage()), req);
  }

  @ExceptionHandler(DuplicateException.class)
  public ResponseEntity<Object> handleDuplicationViolation(ResourceNotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, List.of(ex.getMessage()), req);
  }

  /* ---------- 404: aucune route ne correspond (optionnel, voir props ci-dessous) ---------- */

  protected ResponseEntity<Object> handleNoHandlerFoundException(
          NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status,
          WebRequest webRequest) {

    HttpServletRequest req = (HttpServletRequest) webRequest.resolveReference(WebRequest.REFERENCE_REQUEST);
    String msg = "Route non trouvée: " + ex.getHttpMethod() + " " + ex.getRequestURL();
    return build(HttpStatus.NOT_FOUND, List.of(msg), req);
  }


  /* ---------- Fallback ---------- */

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Erreur interne"), req);
  }

}
