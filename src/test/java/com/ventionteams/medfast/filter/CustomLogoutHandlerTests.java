package com.ventionteams.medfast.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.InvalidTokenException;
import com.ventionteams.medfast.exception.auth.UserNotFoundException;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import com.ventionteams.medfast.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class CustomLogoutHandlerTests {

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomLogoutHandler customLogoutHandler;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Authentication authentication;

  @Test
  void testLogout_Success() {
    String jwt = "mockJwtToken";
    String email = "user@example.com";
    User user = new User();

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtService.extractUserName(jwt)).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    customLogoutHandler.logout(request, response, authentication);

    verify(jwtService).blacklistToken(jwt);
    verify(refreshTokenRepository).deleteByUser(user);
  }

  @Test
  void testLogout_InvalidToken() {
    String jwt = "invalidJwtToken";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtService.extractUserName(jwt)).thenThrow(
        new InvalidTokenException("AuthHeader", "Invalid token"));

    InvalidTokenException thrown = assertThrows(
        InvalidTokenException.class,
        () -> customLogoutHandler.logout(request, response, authentication)
    );

    assertEquals("Error for [AuthHeader]: Invalid token", thrown.getMessage());

    verify(jwtService, never()).blacklistToken(anyString());
    verify(refreshTokenRepository, never()).deleteByUser(any());
  }

  @Test
  void testLogout_NoToken() {
    when(request.getHeader("Authorization")).thenReturn(null);

    InvalidTokenException thrown = assertThrows(
        InvalidTokenException.class,
        () -> customLogoutHandler.logout(request, response, authentication)
    );

    assertEquals("Error for [AuthHeader]: Authorization token is missing or invalid.",
        thrown.getMessage());

    verify(jwtService, never()).extractUserName(anyString());
    verify(jwtService, never()).blacklistToken(anyString());
    verify(refreshTokenRepository, never()).deleteByUser(any());

  }

  @Test
  void testLogout_UserNotFound() {
    String jwt = "validJwtToken";
    String email = "user@example.com";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtService.extractUserName(jwt)).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    UserNotFoundException thrown = assertThrows(
        UserNotFoundException.class,
        () -> customLogoutHandler.logout(request, response, authentication)
    );

    assertEquals("User with email 'user@example.com' not found.", thrown.getMessage());

    verify(jwtService, never()).blacklistToken(anyString());
    verify(refreshTokenRepository, never()).deleteByUser(any());
  }
}
