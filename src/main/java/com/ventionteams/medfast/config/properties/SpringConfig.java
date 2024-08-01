package com.ventionteams.medfast.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring")
public class SpringConfig {
    private Mail mail;

    @Data
    public static class Mail {
        private String username;
    }
}
