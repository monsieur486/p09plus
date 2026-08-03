package com.mr486.commun.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps de réponse renvoyé par les API lorsqu'une requête échoue.
 *
 * <p>Tous les microservices partagent cette structure : un client peut donc traiter
 * les erreurs de la même façon quel que soit le service appelé.</p>
 *
 * <p><b>Exemple :</b> une demande portant sur un patient inexistant produit
 * {@code {"timestamp":"2026-08-01T09:12:33Z","microserviceName":"ms-patients",
 * "path":"/patients/42","errorCode":"NOT_FOUND","messages":["Aucun patient avec l'id: 42"]}}.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// creedengo demande de rendre ces champs finaux ; c'est impossible ici. Lombok génère un
// constructeur sans argument et des mutateurs, qu'un champ final interdirait — la classe ne
// compilerait plus. L'objet est de toute façon assemblé une fois par le builder, puis lu.
@SuppressWarnings("creedengo-java:GCI82")
public class ErrorResponse {

    /** Instant de survenue de l'erreur, au format ISO-8601. */
    private String timestamp;

    /** Nom du microservice ayant produit l'erreur, utile en environnement distribué. */
    private String microserviceName;

    /** Chemin de la requête à l'origine de l'erreur. */
    private String path;

    /** Code d'erreur applicatif, repris du statut HTTP (par exemple {@code NOT_FOUND}). */
    private String errorCode;

    /** Messages explicatifs destinés à l'appelant ; plusieurs en cas d'erreurs de validation. */
    private List<String> messages;
}
