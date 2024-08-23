package com.ventionteams.medfast.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.service.AppointmentService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Tests the appointment controller functionality with integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(PostgreContainerExtension.class)
public class AppointmentControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AppointmentService appointmentService;

  @Test
  public void getAppointments_ExistingUser_ReturnsOk() throws Exception {
    User mockUser = mock(User.class);
    Person mockPerson = mock(Person.class);
    when(mockUser.getPerson()).thenReturn(mockPerson);

    List<AppointmentResponse> mockAppointments = List.of(
        new AppointmentResponse(), new AppointmentResponse()
    );

    when(appointmentService.getAppointments(Optional.of(mockPerson), Optional.empty(),
        AppointmentRequestType.UPCOMING)).thenReturn(mockAppointments);

    ResultActions response = mockMvc.perform(get("/api/patient/appointments")
        .param("type", "UPCOMING")
        .with(user(mockUser)));

    response.andExpect(status().isOk());
  }

  @Test
  public void getAppointments_NoUser_ReturnsForbidden() throws Exception {
    when(appointmentService.getAppointments(Optional.empty(), Optional.empty(),
        AppointmentRequestType.UPCOMING)).thenReturn(List.of());

    ResultActions response = mockMvc.perform(get("/api/patient/appointments")
        .param("type", "UPCOMING"));

    response.andExpect(status().isForbidden());
  }

  @Test
  public void getAppointments_AmountIs1_Returns1Appointment() throws Exception {
    User user = mock(User.class);
    Person mockPerson = mock(Person.class);
    when(user.getPerson()).thenReturn(mockPerson);

    List<AppointmentResponse> mockAppointments = List.of(
        AppointmentResponse.builder()
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
  public void getAppointments_NegativeAmount_ReturnsBadRequest() throws Exception {
    User mockUser = mock(User.class);
    Person mockPerson = mock(Person.class);

    when(mockUser.getPerson()).thenReturn(mockPerson);
    when(appointmentService.getAppointments(Optional.of(mockPerson), Optional.of(-1),
        AppointmentRequestType.UPCOMING)).thenThrow(NegativeAppointmentsAmountException.class);

    ResultActions response = mockMvc.perform(get("/api/patient/appointments")
        .param("amount", "-1")
        .param("type", "UPCOMING")
        .with(user(mockUser)));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void getAppointments_InvalidType_ReturnsBadRequest() throws Exception {
    User mockUser = mock(User.class);

    ResultActions response = mockMvc.perform(get("/api/patient/appointments")
        .param("type", "INVALID_TYPE")
        .with(user(mockUser)));

    response.andExpect(status().isBadRequest());
  }
}
