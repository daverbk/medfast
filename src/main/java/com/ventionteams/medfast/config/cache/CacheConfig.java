package com.ventionteams.medfast.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration.
 */
@Configuration
public class CacheConfig {

  /**
   * Configures and provides a CacheManager bean for managing caches.
   */
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("blacklistedTokens");

    cacheManager.setCaffeine(Caffeine
        .newBuilder()
        .expireAfterWrite(Duration.ofSeconds(3600))
    );

    return cacheManager;
  }
}
