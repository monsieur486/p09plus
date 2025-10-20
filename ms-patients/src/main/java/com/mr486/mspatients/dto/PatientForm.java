package com.mr486.mspatients.dto;

import com.mr486.mspatients.model.Patient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientForm {
  @NotEmpty(message = "First name cannot be empty")
  @NotNull(message = "First name cannot be null")
  private String firstName;

  @NotEmpty(message = "Last name cannot be empty")
  @NotNull(message = "Last name cannot be null")
  private String lastName;

  @NotNull(message = "Birth date cannot be null")
  private LocalDate birthDate;

  @NotEmpty(message = "Gender cannot be empty")
  @NotNull(message = "Gender cannot be null")
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
