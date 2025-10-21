package com.mr486.mspatients.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientUpdateForm {
  private String postalAddress;
  private String phoneNumber;
}
