package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.VerificationConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.repository.VerificationTokenRepository;
import com.ventionteams.medfast.service.UserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for the user verification token entity.
 */
@Service
@RequiredArgsConstructor
public class VerificationTokenService {

  private final VerificationTokenRepository verificationTokenRepository;
  private final UserService userService;
  private final VerificationConfig verificationConfig;

  /**
   * Adds a verification token for the user with the given email.
   */
  @Transactional
  public void addVerificationTokenForUser(String email) {
    UUID securityCode = UUID.randomUUID();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(securityCode.toString());
    verificationToken.setUser(userService.getUserByEmail(email));
    verificationTokenRepository.save(verificationToken);
  }

  /**
   * Verifies the user with the given email and verification token.
   */
  @Transactional
  public boolean verify(String email, String verificationToken) {
    User user = userService.getUserByEmail(email);
    if (user.isEnabled()) {
      deleteVerificationToken(email);
      throw new UserIsAlreadyVerifiedException(email, "User is already verified");
    }

    if (validateToken(email, verificationToken)) {
      user.setEnabled(true);
      userService.save(user);
      deleteVerificationToken(email);
      return true;
    } else {
      return false;
    }
  }

  private boolean validateToken(String email, String verificationToken) {
    VerificationToken token = getVerificationTokenByUserEmail(email);
    long actualValidityPeriod = Duration.between(token.getCreatedDate(), LocalDateTime.now())
        .getSeconds();
    if (actualValidityPeriod > verificationConfig.code().timeout()) {
      deleteVerificationToken(email);
      return false;
    }

    return token.getToken().equals(verificationToken);
  }

  public VerificationToken getVerificationTokenByUserEmail(String email) {
    return verificationTokenRepository.findByUserEmail(email)
        .orElseThrow(() -> new TokenNotFoundException(email, "Token not found"));
  }

  public void deleteVerificationToken(String email) {
    VerificationToken token = getVerificationTokenByUserEmail(email);
    verificationTokenRepository.deleteById(token.getId());
  }
}
