package com.mr486.mswebclient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientAddForm {
  @NotBlank(message = "Le champ prénom est obligatoire")
  private String firstName;
  @NotBlank(message = "Le champ nom de famille est obligatoire")
  private String lastName;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull(message = "Le champ date de naissance est obligatoire")
  private LocalDate birthDate;
  @NotBlank(message = "Le champ genre est obligatoire")
  private String gender;
  private String postalAddress;
  private String phoneNumber;
}
