package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring")
public record SpringConfig(
    @NotNull(message = "spring.mail must not be null")
    Mail mail
) {
    public record Mail(
        @NotBlank(message = "spring.mail.username must not be blank")
        @Email(message = "spring.mail.username must follow the format user@example.com")
        String username
    ) {}
}
