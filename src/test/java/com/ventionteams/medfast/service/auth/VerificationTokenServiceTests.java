package com.ventionteams.medfast.service.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.VerificationConfig;
import com.ventionteams.medfast.config.properties.VerificationConfig.Code;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.repository.VerificationTokenRepository;
import com.ventionteams.medfast.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks verification token service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTests {

  @Mock
  private VerificationTokenRepository verificationTokenRepository;

  @Mock
  private UserService userService;

  @Mock
  private VerificationConfig verificationConfig;

  @InjectMocks
  private VerificationTokenService verificationTokenService;

  @Test
  public void addVerificationTokenForUser_UserExists_TokenSaved() {
    String email = "test@example.com";
    User user = User.builder().email(email).build();

    when(userService.getUserByEmail(email)).thenReturn(user);

    verificationTokenService.addVerificationTokenForUser(email);

    verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
  }

  @Test
  public void verify_UserAlreadyVerified_ExceptionThrown() {
    String email = "test@example.com";
    String token = "exampleToken";
    User user = User.builder().email(email).enabled(true).build();
    VerificationToken verificationToken = mock(VerificationToken.class);

    when(userService.getUserByEmail(email)).thenReturn(user);
    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.ofNullable(verificationToken));

    Assertions.assertThrows(UserIsAlreadyVerifiedException.class, () -> {
      verificationTokenService.verify(email, token);
    });
    verify(verificationTokenRepository, times(1)).deleteById(any());
  }

  @Test
  public void verify_InvalidToken_ExceptionThrown() {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken("valid");
    verificationToken.setCreatedDate(LocalDateTime.now().minusMinutes(2));
    Code code = mock(Code.class);
    String token = "invalidToken";

    when(userService.getUserByEmail(email)).thenReturn(user);
    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.of(verificationToken));
    when(verificationConfig.code()).thenReturn(code);
    when(code.timeout()).thenReturn(3600);

    boolean result = verificationTokenService.verify(email, token);

    Assertions.assertFalse(result);
    Assertions.assertFalse(user.isEnabled());
    verify(verificationTokenRepository, times(0)).deleteById(any());
  }

  @Test
  public void verify_ExpiredToken_VerificationFailed() {
    String email = "test@example.com";
    String token = "invalidToken";
    User user = User.builder().email(email).enabled(false).build();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken(token);
    verificationToken.setCreatedDate(LocalDateTime.now().minusHours(2));
    Code code = mock(Code.class);

    when(userService.getUserByEmail(email)).thenReturn(user);
    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.of(verificationToken));
    when(verificationConfig.code()).thenReturn(code);
    when(code.timeout()).thenReturn(3600);

    boolean result = verificationTokenService.verify(email, token);

    Assertions.assertFalse(result);
    Assertions.assertFalse(user.isEnabled());
    verify(verificationTokenRepository, times(1)).deleteById(any());
  }

  @Test
  void verify_ValidToken_SuccessfulVerification() {
    String email = "test@example.com";
    String token = "invalidToken";
    User user = User.builder().email(email).enabled(false).build();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken(token);
    verificationToken.setCreatedDate(LocalDateTime.now().minusMinutes(2));
    Code code = mock(Code.class);

    when(userService.getUserByEmail(email)).thenReturn(user);
    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.of(verificationToken));
    when(verificationConfig.code()).thenReturn(code);
    when(code.timeout()).thenReturn(3600);

    boolean result = verificationTokenService.verify(email, token);

    Assertions.assertTrue(result);
    Assertions.assertTrue(user.isEnabled());
    verify(userService, times(1)).save(user);
    verify(verificationTokenRepository, times(1)).deleteById(any());
  }

  @Test
  void getVerificationTokenByUserEmail_TokenNotFound_ExceptionThrown() {
    String email = "test@example.com";

    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(Optional.empty());

    Assertions.assertThrows(TokenNotFoundException.class, () -> {
      verificationTokenService.getVerificationTokenByUserEmail(email);
    });
  }

  @Test
  void getVerificationTokenByUserEmail_TokenExists_ReturnsToken() {
    String email = "test@example.com";
    VerificationToken verificationToken = new VerificationToken();

    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.of(verificationToken));

    VerificationToken result = verificationTokenService.getVerificationTokenByUserEmail(email);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(verificationToken, result);
  }

  @Test
  void deleteVerificationToken_TokenExists_TokenDeleted() {
    String email = "test@example.com";
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setId(1L);

    when(verificationTokenRepository.findByUserEmail(email)).thenReturn(
        Optional.of(verificationToken));

    verificationTokenService.deleteVerificationToken(email);

    verify(verificationTokenRepository, times(1)).deleteById(verificationToken.getId());
  }
}
