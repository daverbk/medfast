package com.ventionteams.medfast.service.auth;

import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Checks service for generating user verification URL with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class VerificationUrlServiceTests {

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  AppConfig appConfig;

  @InjectMocks
  private VerificationUrlService verificationUrlService;

  @Test
  public void generateVerificationUrl_InvalidEmail_ExceptionThrown() {
    String email = "invalid";

    when(verificationTokenService.getVerificationTokenByUserEmail(email)).thenThrow(
        new TokenNotFoundException(email, "Token not found"));

    Assertions.assertThrows(TokenNotFoundException.class,
        () -> verificationUrlService.generateVerificationUrl(email));
  }

  @Test
  public void generateVerificationUrl_ValidEmail_ReturnsCorrectUrl() {
    String email = "test@example.com";
    String baseUrl = "http://localhost:8080";
    String token = "exampleToken";
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setId(1L);
    verificationToken.setToken("exampleToken");
    verificationToken.setUser(null);
    String expectedUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
        .path("/verify")
        .queryParam("email", email)
        .queryParam("code", token)
        .toUriString();

    when(verificationTokenService.getVerificationTokenByUserEmail(email)).thenReturn(
        verificationToken);
    when(appConfig.baseUrl()).thenReturn(baseUrl);

    String url = verificationUrlService.generateVerificationUrl(email);

    Assertions.assertEquals(expectedUrl, url);
  }
}
