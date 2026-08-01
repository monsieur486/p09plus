package com.mr486.mspatients.exeption;

/**
 * Exception thrown when a requested entity is not found.
 * This custom exception is used to indicate errors related to the absence
 * of an entity in the system.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructs a new ResourceNotFoundException with the specified detail message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
