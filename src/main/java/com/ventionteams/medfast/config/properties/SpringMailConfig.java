package com.ventionteams.medfast.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record SpringMailConfig(String username) {}
