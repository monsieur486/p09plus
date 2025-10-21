package com.mr486.mspatients.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
  String message;
  int errorCode;
  String timestamp;

  /**
   * Constructs an ErrorResponseDto with the specified message and error code.
   *
   * @param message   the error message
   * @param errorCode the error code
   */
  public ErrorResponse(String message, int errorCode) {
    this.message = message;
    this.errorCode = errorCode;
    this.timestamp = java.time.LocalDateTime.now().toString();
  }
}
