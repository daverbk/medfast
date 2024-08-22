package com.ventionteams.medfast.exception.medicaltest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when pdf generation fails.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PdfGenerationException extends RuntimeException {
  public PdfGenerationException(String message) {
    super(String.format("Failed to generate pdf: %s", message));
  }
}