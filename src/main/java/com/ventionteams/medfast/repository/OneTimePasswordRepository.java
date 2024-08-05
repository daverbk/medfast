package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findByUserEmailAndToken(String user, String otp);
}
