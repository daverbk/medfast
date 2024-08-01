package com.ventionteams.medfast.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token")
public class TokenConfig {
    private Timeout timeout;
    private Signing signing;
    @Data
    public static class Timeout{
        private long access;
        private long refresh;
    }
    @Data
    public static class Signing {
        private String key;
    }
}
