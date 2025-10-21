package com.mr486.mspatients.dto;

import com.mr486.mspatients.model.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientForm {
  @NotBlank(message = "Le prénom ne peut pas être vide")
  private String firstName;

  @NotBlank(message = "Le nom de famille ne peut pas être vide")
  private String lastName;

  @NotNull(message = "La date de naissance ne peut pas être nulle")
  private LocalDate birthDate;

  @NotBlank(message = "Le genre ne peut pas être vide")
  private String gender;

  private String postalAddress;
  private String phoneNumber;

  public Patient toEntity() {
    Patient patient = new Patient();
    patient.setFirstName(firstName);
    patient.setLastName(lastName);
    patient.setBirthDate(birthDate);
    patient.setGender(gender);
    patient.setPostalAddress(postalAddress);
    patient.setPhoneNumber(phoneNumber);
    return patient;
  }
}
