package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "verification.code")
public record VerificationCodeConfig(
    @NotNull(message = "verification.code.timeout must not be null")
    @PositiveOrZero(message = "verification.code.timeout must be greater or equal to 0")
    int timeout
) {}
