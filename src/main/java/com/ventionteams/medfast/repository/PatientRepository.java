package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Patient;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
