package com.ventionteams.medfast.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties
public class AppConfig {
    private SpringConfig spring;
    private TokenConfig token;
    private VerificationConfig verification;
    private String baseUrl;

}
