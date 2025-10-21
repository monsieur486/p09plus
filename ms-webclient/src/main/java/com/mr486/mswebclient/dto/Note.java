package com.mr486.mswebclient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
  @NotBlank(message = "Le contenu de la note ne peut pas être vide")
  private String content;
}
