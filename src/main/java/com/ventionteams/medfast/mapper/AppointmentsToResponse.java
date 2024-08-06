package com.ventionteams.medfast.mapper;

import com.ventionteams.medfast.dto.response.ConsultationAppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class AppointmentsToResponse implements Function<List<ConsultationAppointment>,
    List<ConsultationAppointmentResponse>> {
    @Override
    public List<ConsultationAppointmentResponse> apply(List<ConsultationAppointment> consultationAppointments) {
        return consultationAppointments.stream()
            .map(appointment -> {
                String locationString = Optional.ofNullable(appointment.getLocation())
                    .map(location -> location.getHospitalName().concat(", ")
                        .concat(location.getHouse())
                        .concat(" ")
                        .concat(location.getStreetAddress()))
                    .orElse("");

                return ConsultationAppointmentResponse.builder()
                    .id(appointment.getId())
                    .doctorsId(appointment.getDoctor().getId())
                    .doctorsSpecialization(appointment.getDoctor().getSpecializations()
                        .get(0).getSpecialization()) // what specialization should be written?
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
