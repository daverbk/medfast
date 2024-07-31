package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.AppProperties;
import com.ventionteams.medfast.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class VerificationUrlService {
    private final VerificationTokenService verificationTokenService;
    private final AppProperties appProperties; //bean with variables from application.yml

    public String generateVerificationUrl(String email) {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        VerificationToken verificationToken = verificationTokenService.getVerificationTokenByUserEmail(email);
        return UriComponentsBuilder.fromHttpUrl(appProperties.getBasUrl()) //base-url
            .path("/verify")
            .queryParam("email", encodedEmail)
            .queryParam("code", verificationToken.getToken())
            .toUriString();
    }
}
