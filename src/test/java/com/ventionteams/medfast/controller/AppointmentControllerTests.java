package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.ConsultationAppointmentResponse;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    public void getSortedAppointments_ExistingUser_ReturnsOk() throws Exception {
        User mockUser = mock(User.class);
        Person mockPerson = mock(Person.class);
        when(mockUser.getPerson()).thenReturn(mockPerson);

        List<ConsultationAppointmentResponse> mockAppointments = List.of(
            new ConsultationAppointmentResponse(), new ConsultationAppointmentResponse()
        );

        when(appointmentService.getAppointments(Optional.of(mockPerson), Optional.empty(),
            AppointmentRequestType.UPCOMING)).thenReturn(mockAppointments);

        ResultActions response = mockMvc.perform(get("/api/patient/appointments")
            .param("type", "UPCOMING")
            .with(user(mockUser)));

        response.andExpect(status().isOk());
    }

    @Test
    public void getSortedAppointments_NoUser_ReturnsForbidden() throws Exception {
        when(appointmentService.getAppointments(Optional.empty(), Optional.empty(),
            AppointmentRequestType.UPCOMING)).thenReturn(List.of());

        ResultActions response = mockMvc.perform(get("/api/patient/appointments")
            .param("type", "UPCOMING"));

        response.andExpect(status().isForbidden());
    }

    @Test
    public void getSortedAppointments_AmountIs1_Returns1Appointment() throws Exception {
        User user = mock(User.class);
        Person mockPerson = mock(Person.class);
        when(user.getPerson()).thenReturn(mockPerson);

        List<ConsultationAppointmentResponse> mockAppointments = List.of(
            ConsultationAppointmentResponse.builder()
                .id(1).doctorsId(2).doctorsName("John").build()
        );

        when(appointmentService.getAppointments(
            Optional.of(mockPerson),
            Optional.of(1),
            AppointmentRequestType.UPCOMING
        )).thenReturn(mockAppointments);

        ResultActions response = mockMvc.perform(get("/api/patient/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .param("type", "UPCOMING")
            .param("amount", "1")
            .with(user(user)));

        response.andExpect(jsonPath("$.data.length()").value(1)).andDo(print());
    }

    @Test
    public void getSortedAppointments_InvalidType_ReturnsBadRequest() throws Exception {
        User mockUser = mock(User.class);

        ResultActions response = mockMvc.perform(get("/api/patient/appointments")
            .param("type", "INVALID_TYPE")
            .with(user(mockUser)));

        response.andExpect(status().isBadRequest());
    }
}
