package com.ventionteams.medfast.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties
public class AppProperties {
    private SpringConfig spring;
    private TokenConfig token;
    private VerificationConfig verification;
    private String basUrl;
    @Data
    public static class SpringConfig {
        private Mail mail;

        @Data
        public static class Mail {
            private String username;
        }
    }

    @Data
    public static class TokenConfig {
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

    @Data
    public static class VerificationConfig{
        private Code code;
        @Data
        public static class Code{
            private int timeout;
        }
    }

}
