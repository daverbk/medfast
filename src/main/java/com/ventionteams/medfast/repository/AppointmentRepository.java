package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Person;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for the Appointment entity.
 */
public interface AppointmentRepository extends JpaRepository<ConsultationAppointment, Long> {

  @Query("SELECT c FROM ConsultationAppointment c "
      + "WHERE c.doctor = :person OR c.patient = :person "
      + "ORDER BY c.dateFrom ASC")
  List<ConsultationAppointment> findAllByPatientOrDoctorOrderByDateFromAsc(
      @Param("person") Person person);
}
