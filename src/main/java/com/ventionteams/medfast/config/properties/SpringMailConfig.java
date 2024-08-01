package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.mail")
public record SpringMailConfig(
    @NotBlank(message = "spring.mail.username must not be blank")
    @Email(message = "spring.mail.username must follow the format user@example.com")
    String username
) {}
