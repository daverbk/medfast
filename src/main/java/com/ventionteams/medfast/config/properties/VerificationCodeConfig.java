package com.ventionteams.medfast.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verification.code")
public record VerificationCodeConfig(int timeout) {}
