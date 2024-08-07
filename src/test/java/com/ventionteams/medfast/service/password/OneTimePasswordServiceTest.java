package com.ventionteams.medfast.service.password;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks the one time password service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class OneTimePasswordServiceTest {

  @Mock
  private OneTimePasswordRepository oneTimePasswordRepository;

  @Mock
  private TokenConfig tokenConfig;

  @Mock
  private Random random;

  @InjectMocks
  private OneTimePasswordService oneTimePasswordService;

  @Test
  public void verify_InvalidToken_ExceptionThrown() {
    String email = "test@example.com";
    String otpToken = "1234";

    when(oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)).thenReturn(
        Optional.empty());

    assertThrows(TokenNotFoundException.class,
        () -> oneTimePasswordService.verify(email, otpToken));
  }

  @Test
  public void verify_ExpiredToken_TimeoutExceptionThrown() {
    String email = "test@example.com";
    String otpToken = "1234";
    OneTimePassword otp = new OneTimePassword();
    otp.setCreatedDate(LocalDateTime.now().minusSeconds(3600));

    when(oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)).thenReturn(
        Optional.of(otp));
    when(tokenConfig.timeout()).thenReturn(new TokenConfig.Timeout(0, 0, 60));

    assertThrows(TokenExpiredException.class, () -> oneTimePasswordService.verify(email, otpToken));
  }

  @Test
  public void generate_ValidUser_OtpGenerated() {
    User user = new User();
    when(random.nextInt(10000)).thenReturn(1234);

    OneTimePassword otp = oneTimePasswordService.generate(user);

    assertNotNull(otp);
    assertEquals(user, otp.getUser());
    assertEquals("1234", otp.getToken());
    verify(oneTimePasswordRepository, times(1)).save(any(OneTimePassword.class));
  }
}
