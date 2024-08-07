package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Mapper that converts a list of consultation appointments to a list of appointment responses.
 */
@Component
public class AppointmentsToResponse implements Function<List<ConsultationAppointment>,
    List<AppointmentResponse>> {

  @Override
  public List<AppointmentResponse> apply(List<ConsultationAppointment> consultationAppointments) {
    return consultationAppointments.stream()
        .map(appointment -> {
          String locationString = Optional.ofNullable(appointment.getLocation())
              .map(location -> location.getHospitalName().concat(", ")
                  .concat(location.getHouse())
                  .concat(" ")
                  .concat(location.getStreetAddress()))
              .orElse("");

          return AppointmentResponse.builder()
              .id(appointment.getId())
              .doctorsId(appointment.getDoctor().getId())
              .doctorsSpecialization(appointment.getDoctor().getSpecializations()
                  .get(0).getSpecialization())
              .doctorsName(appointment.getDoctor().getName().concat(" ").concat(
                  appointment.getDoctor().getSurname()
              ))
              .dateFrom(appointment.getDateFrom().toString())
              .dateTo(appointment.getDateTo().toString())
              .location(locationString)
              .status(appointment.getStatus().toString())
              .type(appointment.getType())
              .build();
        })
        .toList();
  }
}
