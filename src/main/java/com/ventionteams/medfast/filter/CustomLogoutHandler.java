package com.ventionteams.medfast.filter;

import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.UserNotFoundException;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import com.ventionteams.medfast.service.auth.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logout Handler - handles logout request.
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) {

    String authHeader = request.getHeader("Authorization");
    if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader,
        "Bearer ")) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
          "Authorization token is missing or invalid.");
      return;
    }
    String jwt = authHeader.substring(7);

    try {
      String email = jwtService.extractUserName(jwt);

      User currentUser = userRepository.findByEmail(email).orElseThrow(() ->
          new UserNotFoundException(email));
      jwtService.blacklistToken(jwt);
      refreshTokenRepository.deleteByUser(currentUser);

    } catch (UserNotFoundException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

    } catch (SignatureException | MalformedJwtException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
          "The provided access token has an invalid signature.");

    } catch (Exception e) {
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An unexpected error occurred on the server.");
    }

  }

  //exception handling will be moved to ExceptionHandler shortly
  private void sendError(HttpServletResponse response, int statusCode, String message) {
    try {
      response.sendError(statusCode, message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
