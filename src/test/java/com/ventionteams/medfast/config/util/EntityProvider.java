package com.ventionteams.medfast.config.util;

/**
 * Provides an entity for further integration testing.
 *
 * @param <T> the type of entity to provide
 */
public interface EntityProvider<T> {

  T provide();
}
