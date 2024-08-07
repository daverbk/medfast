package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * JWT service responsible for generating and validating JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final TokenConfig tokenConfig;
  private final CacheManager cacheManager;

  @CachePut(value = "blacklistedTokens", key = "#token", unless = "#result == null")
  public String blacklistToken(String token) {
    return token;
  }

  /**
   * Checks if token is present in the cache.
   */
  public boolean isTokenBlacklisted(String token) {
    Cache cache = cacheManager.getCache("blacklistedTokens");
    if (cache == null) {
      return false;
    }
    return cache.get(token) != null;
  }

  /**
   * Generate a JWT token for the user based on the user data.
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    if (userDetails instanceof User customUserDetails) {
      claims.put("id", customUserDetails.getId());
      claims.put("email", customUserDetails.getEmail());
      claims.put("role", customUserDetails.getRole());
    }
    return generateToken(claims, userDetails);
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusSeconds(tokenConfig.timeout().access())))
        .signWith(getSigningKey())
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public String extractUserName(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(tokenConfig.signing().key());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
