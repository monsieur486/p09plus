package com.mr486.msnotes.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Note rédigée par un praticien, telle qu'elle est stockée dans la base documentaire.
 *
 * <p>Cette entité reste confinée à la couche de persistance : les API exposent le
 * {@code NoteDto} de la bibliothèque commune, qui ne publie que le contenu rédigé.</p>
 *
 * <p><b>Exemple :</b> une note « Le patient déclare qu'il fume depuis peu » rattachée au
 * patient 3 occupe un document de la collection {@code notes}.</p>
 */
@Document(collection = "notes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// creedengo demande de rendre ces champs finaux ; c'est impossible ici. Lombok génère un
// constructeur sans argument et des mutateurs, qu'un champ final interdirait — la classe ne
// compilerait plus. Le pilote MongoDB peuple d'ailleurs le document par ces mutateurs.
@SuppressWarnings("creedengo-java:GCI82")
public class Note {

    /** Identifiant technique du document, attribué par la base. */
    @Id
    private String id;

    /** Identifiant du patient concerné ; indexé car toutes les recherches passent par lui. */
    @Indexed
    private Long patientId;

    /** Contenu rédigé par le praticien, analysé lors de l'évaluation du risque. */
    private String content;

    /** Date d'enregistrement, qui détermine l'ordre d'affichage des notes. */
    private Instant createdDate;
}
