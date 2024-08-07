package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application configuration properties from the spring descendants of application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "spring")
public record SpringConfig(
    @NotNull(message = "spring.mail must not be null")
    Mail mail
) {

  /**
   * Configuration properties for the mail descendants.
   */
  public record Mail(
      @NotBlank(message = "spring.mail.username must not be blank")
      @Email(message = "spring.mail.username must follow the format user@example.com")
      String username
  ) {

  }
}
