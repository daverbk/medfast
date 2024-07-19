package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.exception.auth.TokenRefreshException;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.repository.RefreshTokenRepository;
import com.ventionteams.medfast.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Getter
    @Value("${token.timeout.refresh}")
    private int expirationSeconds;

    @Transactional
    public RefreshToken generateToken(UserDetails userDetails) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByEmail(
            userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found")
        ));

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
            .orElseThrow(() -> new NoSuchElementException("Refresh token is not found"));

        verifyExpiration(refreshToken);
        return new JwtAuthenticationResponse(
            jwtService.generateToken(refreshToken.getUser()),
            requestRefreshToken,
            jwtService.getExpirationSeconds(),
            Duration.between(LocalDateTime.now(), refreshToken.getCreatedDate().plusSeconds(expirationSeconds)).getSeconds()
        );
    }

    private void verifyExpiration(RefreshToken token) {
        if (Duration.between(LocalDateTime.now(), token.getCreatedDate()).getSeconds() > expirationSeconds) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(
                token.getToken(),
                "Refresh token has expired. Please make a new sign in request"
            );
        }
    }
}
