package com.mr486.mswebclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateForm {
  private String postalAddress;
  private String phoneNumber;
}
