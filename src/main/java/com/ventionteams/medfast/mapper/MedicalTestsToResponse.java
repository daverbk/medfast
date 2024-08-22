package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.MedicalTestAppointmentResponse;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of medical tests to a list of medical test responses.
 */
@Component
public class MedicalTestsToResponse implements Function<List<MedicalTestAppointment>,
    List<MedicalTestAppointmentResponse>> {

  @Override
  public List<MedicalTestAppointmentResponse> apply(
      List<MedicalTestAppointment> medicalTestAppointments) {

    return medicalTestAppointments.stream()
      .map(this::testAppointmentToResponse)
      .collect(Collectors.toList());
  }

  /**
   * Converts a medical test to a medical test response.
   */
  public MedicalTestAppointmentResponse testAppointmentToResponse(
      MedicalTestAppointment medicalTestAppointment) {
    MedicalTestAppointmentResponse
        .MedicalTestAppointmentResponseBuilder builder =
        MedicalTestAppointmentResponse.builder()
            .id(medicalTestAppointment.getId())
            .testName(medicalTestAppointment.getTestName())
            .dateOfTest(medicalTestAppointment.getDateOfTest())
            .testCategory(medicalTestAppointment.getTestCategory());
    builder.hasPdfResult(medicalTestAppointment.getPdf() != null);

    if (medicalTestAppointment.getDoctor() != null) {
      builder.doctorsId(medicalTestAppointment.getDoctor().getId())
          .doctorsName(medicalTestAppointment.getDoctor().getPerson().getName().concat(" ")
              .concat(medicalTestAppointment.getDoctor().getPerson().getSurname()));
    }
    return builder.build();
  }
}
