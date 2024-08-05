package com.ventionteams.medfast.config.audit;

import com.ventionteams.medfast.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing()
public class JpaAuditingConfiguration {
    @Bean
    public @NotNull AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication =
                SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
                // DEVNOTE: In case no authentication was done return system as the auditor
                return Optional.of("system");
            }

            User userPrincipal = (User) authentication.getPrincipal();
            return Optional.ofNullable(userPrincipal.getUsername());
        };
    }
}
