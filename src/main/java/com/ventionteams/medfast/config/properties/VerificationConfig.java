package com.ventionteams.medfast.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "verification")
public class VerificationConfig{
    private Code code;
    @Data
    public static class Code{
        private int timeout;
    }
}
