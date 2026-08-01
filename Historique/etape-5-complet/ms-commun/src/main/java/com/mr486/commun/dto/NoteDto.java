package com.mr486.commun.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Note rédigée par un praticien au sujet d'un patient.
 *
 * <p>Seul le contenu textuel transite : l'identifiant du patient est porté par l'URL,
 * et les métadonnées de stockage restent internes au service des notes.</p>
 *
 * <p><b>Exemple :</b> {@code POST /ms-notes/patients/1/notes} avec
 * {@code {"content":"Le patient déclare qu'il fume depuis peu"}} ajoute une note ; un
 * contenu vide retourne un statut 400.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {

    /** Contenu de la note, analysé pour y rechercher les termes déclencheurs. */
    @NotBlank(message = "Le contenu de la note est obligatoire")
    private String content;
}
