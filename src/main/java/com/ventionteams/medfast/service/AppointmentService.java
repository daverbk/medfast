package com.ventionteams.medfast.service;

import com.ventionteams.medfast.dto.response.ConsultationAppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.mapper.AppointmentsToResponse;
import com.ventionteams.medfast.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentsToResponse appointmentsToResponse;

    @Transactional
    public List<ConsultationAppointmentResponse> getAppointments(Optional<Person> person,
                                                                 Optional<Integer> amount,
                                                                 AppointmentRequestType type) {
        int appointmentAmount = amount.orElse(0);
        if (appointmentAmount < 0) {
            throw new NegativeAppointmentsAmountException("If amount param presented then it must be positive or zero");
        }
        return getFilteredAppointments(person, type, appointmentAmount);
    }

    private List<ConsultationAppointmentResponse> getFilteredAppointments(Optional<Person> person,
                                                                          AppointmentRequestType type, int amount) {
        LocalDateTime now = LocalDateTime.now();

        return person
            .map(p -> repository.findAllByPatientOrDoctorOrderByDateFromAsc(p).stream()
                .filter(appointment -> switch (type) {
                    case PAST -> appointment.getDateFrom().isBefore(now);
                    case UPCOMING -> appointment.getDateFrom().isAfter(now);
                })
                .sorted(Comparator.comparing(ConsultationAppointment::getDateFrom))
                .limit(amount > 0 ? amount : Long.MAX_VALUE)
                .toList())
            .map(appointmentsToResponse)
            .orElseGet(ArrayList::new);
    }
}
