package com.mr486.mspatients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
  String timestamp;
  String microserviceName;
  String path;
  String errorCode;
  List<String> messages;
}
