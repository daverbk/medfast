package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for the MedicalTest entity.
 */
public interface MedicalTestAppointmentRepository
    extends JpaRepository<MedicalTestAppointment, Long> {

  List<MedicalTestAppointment> findAllByPatientOrderByDateOfTestDesc(@Param("user") User user);

  @Modifying
  @Query("UPDATE MedicalTestAppointment t SET t.pdf = :pdf WHERE t.id = :id")
  void setPdfById(byte[] pdf, Long id);

  @Query("SELECT t FROM MedicalTestAppointment t WHERE DATE(t.dateOfTest) = :date")
  List<MedicalTestAppointment> findTestAppointmentsByDate(@Param("date") LocalDate date);
}
