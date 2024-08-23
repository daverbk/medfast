package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks appointments mapper functionality with unit tests.
 */
public class AppointmentsToResponseTests {

  Doctor doctor;
  Specialization specialization;
  Location location;
  List<ConsultationAppointment> consultationAppointmentList;
  List<AppointmentResponse> appointmentResponseList;
  AppointmentsToResponse appointmentsToResponse;

  @BeforeEach
  void setUp() {
    appointmentsToResponse = new AppointmentsToResponse();

    doctor = Doctor.builder().id(1L).name("John").surname("Doe").build();

    specialization = new Specialization();
    specialization.setSpecialization("Cardiology");

    doctor.setSpecializations(List.of(specialization));

    location = Location.builder().hospitalName("General Hospital").house("123")
        .streetAddress("Main Street").build();

    consultationAppointmentList = List.of(
        ConsultationAppointment.builder()
            .id(1L)
            .doctor(doctor)
            .patient(null)
            .serviceId(1L)
            .dateFrom(LocalDateTime.of(2023, 8, 10, 10, 0))
            .dateTo(LocalDateTime.of(2023, 8, 10, 11, 0))
            .type("on-site")
            .status(AppointmentStatus.IN_CONSULTATION)
            .build()
    );

    appointmentResponseList = List.of(
        AppointmentResponse.builder()
            .id(1L)
            .doctorsId(1L)
            .doctorsSpecialization("Cardiology")
            .doctorsName("John Doe")
            .dateFrom("2023-08-10T10:00")
            .dateTo("2023-08-10T11:00")
            .status("In-Consultation")
            .type("on-site")
            .build()
    );


  }

  @Test
  public void apply_FullInformation_ReturnFullInformationResponse() {
    consultationAppointmentList.get(0).setLocation(location);
    appointmentResponseList.get(0).setLocation("General Hospital, 123 Main Street");

    List<AppointmentResponse> appointmentResponses = appointmentsToResponse.apply(
        consultationAppointmentList);

    Assertions.assertThat(appointmentResponses).isEqualTo(appointmentResponseList);
  }

  @Test
  public void apply_MissingLocation_ReturnResponseWithoutLocation() {
    appointmentResponseList.get(0).setLocation("");

    List<AppointmentResponse> appointmentResponses = appointmentsToResponse.apply(
        consultationAppointmentList);

    Assertions.assertThat(appointmentResponses).isEqualTo(appointmentResponseList);
  }
}
