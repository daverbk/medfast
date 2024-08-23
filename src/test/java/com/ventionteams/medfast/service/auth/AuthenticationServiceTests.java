package com.ventionteams.medfast.service.auth;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.config.properties.TokenConfig.Timeout;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Checks authentication service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private EmailService emailService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  private TokenConfig tokenConfig;

  @InjectMocks
  private AuthenticationService authenticationService;

  @Test
  public void signUp_EmailAuthenticationFails_ExceptionThrown() throws MessagingException {
    SignUpRequest request = new SignUpRequest("test@example.com", "qweRTY123$", "John",
        "Doe", LocalDate.now().minusYears(20), "Main street", "123", "42 a", "Chicago",
        "Illinios", "60007", "12345678900", "male", "Canada");
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.getUserByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doThrow(MailAuthenticationException.class).when(emailService).sendVerificationEmail(user);

    Assertions.assertThrows(MailAuthenticationException.class,
        () -> authenticationService.signUp(request));
    verify(emailService, times(3)).sendVerificationEmail(user);
  }

  @Test
  public void signUp_UserAlreadyEnabled_ExceptionThrown() {
    SignUpRequest request = new SignUpRequest("test@example.com", "qweRTY123$", "John",
        "Doe", LocalDate.now().minusYears(20), "Main street", "123", "42 a", "Chicago",
        "Illinios", "60007", "12345678900", "male", "Canada");
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(true).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.getUserByEmail(email)).thenReturn(user);

    Assertions.assertThrows(UserIsAlreadyVerifiedException.class,
        () -> authenticationService.signUp(request));
  }

  @Test
  public void signUp_NoUserExists_UserCreated() throws MessagingException, IOException {
    SignUpRequest request = new SignUpRequest("test@example.com", "qweRTY123$", "John",
        "Doe", LocalDate.now().minusYears(20), "Main street", "123", "42 a", "Chicago",
        "Illinios", "60007", "12345678900", "male", "Canada");
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.create(request)).thenReturn(user);
    when(userService.getUserByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doNothing().when(emailService).sendVerificationEmail(user);

    String response = authenticationService.signUp(request);

    Assertions.assertEquals("Email verification link has been sent to your email", response);
    verify(userService).create(request);
    verify(emailService).sendVerificationEmail(user);
  }

  @Test
  public void sendVerificationEmail_UserWithEmailNoExists_ExceptionThrown() {
    String email = "invalid@example.com";

    when(userService.getUserByEmail(email)).thenThrow(UsernameNotFoundException.class);

    Assertions.assertThrows(UsernameNotFoundException.class,
        () -> authenticationService.sendVerificationEmail(email));
  }

  @Test
  public void sendVerificationEmail_UserAlreadyEnabled_ExceptionThrown() {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(true).build();

    when(userService.getUserByEmail(email)).thenReturn(user);

    Assertions.assertThrows(UserIsAlreadyVerifiedException.class,
        () -> authenticationService.sendVerificationEmail(email));
  }

  @Test
  public void sendVerificationEmail_EmailAuthenticationFails_ExceptionThrown()
      throws MessagingException {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.getUserByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doThrow(MailAuthenticationException.class).when(emailService).sendVerificationEmail(user);

    Assertions.assertThrows(MailAuthenticationException.class,
        () -> authenticationService.sendVerificationEmail(email));
    verify(emailService, times(3)).sendVerificationEmail(user);
  }

  @Test
  public void sendVerificationEmail_ValidEmail_SendEmail() throws MessagingException, IOException {
    String email = "test@example.com";
    User user = User.builder().email(email).enabled(false).build();

    when(userService.getUserByEmail(email)).thenReturn(user);
    doNothing().when(verificationTokenService).addVerificationTokenForUser(email);
    doNothing().when(emailService).sendVerificationEmail(user);

    authenticationService.sendVerificationEmail(email);

    verify(userService).getUserByEmail(email);
    verify(verificationTokenService).addVerificationTokenForUser(email);
    verify(emailService).sendVerificationEmail(user);
  }

  @Test
  public void signIn_InvalidUsernamePassword_ExceptionThrown() {
    SignInRequest request = new SignInRequest("test@example.com", "qweRTY123$");

    doThrow(BadCredentialsException.class).when(authenticationManager)
        .authenticate(new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));

    Assertions.assertThrows(AuthenticationException.class,
        () -> authenticationService.signIn(request));
  }

  @Test
  public void singIn_GoodCredentials_ReturnsJwtResponse() {
    User user = new User();
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken("exampleRefreshToken");
    UserDetailsService userDetailsService = mock(UserDetailsService.class);
    Timeout timeout = mock(Timeout.class);
    SignInRequest request = new SignInRequest("test@example.com", "qweRTY123$");

    when(userService.getUserDetailsService()).thenReturn(userDetailsService);
    when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("exampleToken");
    when(refreshTokenService.generateToken(user)).thenReturn(refreshToken);
    when(tokenConfig.timeout()).thenReturn(timeout);
    when(timeout.access()).thenReturn(3600L);
    when(timeout.refresh()).thenReturn(7200L);

    JwtAuthenticationResponse response = authenticationService.signIn(request);

    Assertions.assertEquals("exampleToken", response.getAccessToken());
    Assertions.assertEquals("exampleRefreshToken", response.getRefreshToken());
  }
}
