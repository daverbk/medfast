package com.ventionteams.medfast.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
public record TokenConfig(Timeout timeout, Signing signing) {
    public record Timeout(long access, long refresh) {}
    public record Signing(String key) {}
}

