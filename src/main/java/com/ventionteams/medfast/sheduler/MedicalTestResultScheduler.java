package com.ventionteams.medfast.sheduler;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.exception.medicaltest.InvalidMedicalTestDataException;
import com.ventionteams.medfast.service.MedicalTestAppointmentService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * .
 */
@Component
@RequiredArgsConstructor
public class MedicalTestResultScheduler {

  private final MedicalTestAppointmentService medicalTestAppointmentService;

  /**
   * A scheduler that runs every day at a set time.
   */

  @Scheduled(cron = "0 15 08 * * ?", zone = "GMT+2")
  //use cron = "0 * * * * ?" for testing (every minute)
  public void generateTestsForAllAppointments() {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    List<MedicalTestAppointment> appointments = medicalTestAppointmentService
        .findTestAppointmentsByDate(yesterday);

    for (MedicalTestAppointment appointment : appointments) {
      try {
        medicalTestAppointmentService
            .generateTestResultFormAppointmentForScheduler(appointment.getId());
      } catch (InvalidMedicalTestDataException e) {
        continue;
      }
    }
  }
}
