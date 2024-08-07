package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service for generating user verification URL.
 */
@Service
@RequiredArgsConstructor
public class VerificationUrlService {

  private final VerificationTokenService verificationTokenService;
  private final AppConfig appConfig;

  /**
   * Generates the verification URL for the user with the given email consisting of the email and a
   * verification code.
   */
  public String generateVerificationUrl(String email) {
    VerificationToken verificationToken = verificationTokenService.getVerificationTokenByUserEmail(
        email);
    return UriComponentsBuilder.fromHttpUrl(appConfig.baseUrl())
        .path("/verify")
        .queryParam("email", email)
        .queryParam("code", verificationToken.getToken())
        .toUriString();
  }
}
