package com.ventionteams.medfast.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties
public record AppConfig(
    @NotBlank(message = "baseUrl must not be blank")
    String baseUrl
) {}
