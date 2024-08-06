package com.ventionteams.medfast.service;

import com.ventionteams.medfast.dto.response.ConsultationAppointmentResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.mapper.AppointmentsToResponse;
import com.ventionteams.medfast.repository.AppointmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentsToResponse appointmentsToResponse;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void getAppointments_EmptyPerson_ReturnsEmptyArrayList() {
        Optional<Person> person = Optional.empty();
        Optional<Integer> amount = Optional.empty();
        AppointmentRequestType type = AppointmentRequestType.UPCOMING;

        List<ConsultationAppointmentResponse> appointments = appointmentService
            .getAppointments(person, amount, type);

        Assertions.assertThat(appointments).isEmpty();
    }

    @Test
    void getAppointments_NegativeAmount_ExceptionThrown() {
        Optional<Person> person = Optional.empty();
        Optional<Integer> amount = Optional.of(-5);
        AppointmentRequestType type = AppointmentRequestType.UPCOMING;

        Assertions.assertThatThrownBy(() -> appointmentService.getAppointments(person, amount, type))
            .isInstanceOf(NegativeAppointmentsAmountException.class);
    }

    @Test
    void getAppointments_UpcomingType_ReturnsArrayList() {
        Optional<Person> person = Optional.ofNullable(Patient.builder()
            .id(1L)
            .checkboxTermsAndConditions(false)
            .build());
        Optional<Integer> amount = Optional.of(5);
        AppointmentRequestType type = AppointmentRequestType.UPCOMING;
        List<ConsultationAppointment> consultationAppointments = List.of(
            ConsultationAppointment.builder()
                .dateFrom(LocalDateTime.now().plusDays(1))
                .build()
        );

        when(appointmentRepository.findAllByPatientOrDoctorOrderByDateFromAsc(person.get()))
            .thenReturn(consultationAppointments);

        List<ConsultationAppointmentResponse> consultationAppointmentResponses = appointmentService
            .getAppointments(person, amount, type);

        Assertions.assertThat(consultationAppointmentResponses).isEqualTo(
            appointmentsToResponse.apply(consultationAppointments)
        );
    }

    @Test
    void getAppointments_PastType_ReturnsArrayList() {
        Optional<Person> person = Optional.ofNullable(Patient.builder()
            .id(1L)
            .checkboxTermsAndConditions(false)
            .build());
        Optional<Integer> amount = Optional.of(5);
        AppointmentRequestType type = AppointmentRequestType.PAST;
        List<ConsultationAppointment> consultationAppointments = List.of(
            ConsultationAppointment.builder()
                .dateFrom(LocalDateTime.now().minusDays(1))
                .build()
        );

        when(appointmentRepository.findAllByPatientOrDoctorOrderByDateFromAsc(person.get()))
            .thenReturn(consultationAppointments);

        List<ConsultationAppointmentResponse> consultationAppointmentResponses = appointmentService
            .getAppointments(person, amount, type);

        Assertions.assertThat(consultationAppointmentResponses).isEqualTo(
            appointmentsToResponse.apply(consultationAppointments)
        );
    }

}
