package com.ventionteams.medfast.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.dto.request.CreateMedicalTestAppointmentRequest;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.MedicalTestCategory;
import com.ventionteams.medfast.exception.medicaltest.BadCredentialsForMedicalTest;
import com.ventionteams.medfast.exception.medicaltest.InvalidMedicalTestDataException;
import com.ventionteams.medfast.service.MedicalTestAppointmentService;
import java.time.LocalDate;
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
 * Tests the medical test controller functionality with integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(PostgreContainerExtension.class)
public class MedicalTestAppointmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MedicalTestAppointmentService medicalTestAppointmentService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void saveTest_ValidRequest_ReturnsOk() throws Exception {
    CreateMedicalTestAppointmentRequest request = new CreateMedicalTestAppointmentRequest();
    request.setDateOfTest(LocalDate.now().plusDays(1));
    request.setTestCategory(MedicalTestCategory.MRI);
    request.setPatientEmail("patient@gmail.com");
    request.setTestName("Vitamin D");
    User mockUser = mock(User.class);

    ResultActions response = mockMvc.perform(post("/api/patient/tests/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(user(mockUser)));
    response.andExpect(status().isOk())
        .andExpect(jsonPath("$.status")
            .value(200))
        .andExpect(jsonPath("$.message")
            .value("Test has been created successfully"));
  }


  @Test
  public void getSortedTests_ValidRequest_ReturnsOk() throws Exception {
    User mockUser = mock(User.class);
    ResultActions response = mockMvc.perform(get("/api/patient/tests")
        .param("type", String.valueOf(AppointmentRequestType.PAST))
        .with(user(mockUser)));

    response.andExpect(status().isOk())
        .andExpect(jsonPath("$.status")
            .value(200))
        .andExpect(jsonPath("$.message")
            .value("Operation successful"));
  }

  @Test
  public void getTestResult_ValidRequest_ReturnsOkAndHeaderWithContent() throws Exception {
    User mockUser = mock(User.class);
    byte[] byteArray = new byte[]{(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20};

    doReturn("test.pdf")
        .when(medicalTestAppointmentService).getPdfName(any(Long.class));

    when(medicalTestAppointmentService.getTestResult(any(User.class), any(Long.class)))
        .thenReturn(byteArray);

    ResultActions response = mockMvc.perform(get("/api/patient/tests/result")
        .param("testId", "1")
        .with(user(mockUser)));

    response.andExpect(status().isOk())
        .andExpect(header().exists("Content-Length"))
        .andExpect(header().string("Content-Length",
            org.hamcrest.Matchers.greaterThan("0")));
  }

  @Test
  public void generateTestResultForAppointment_ValidRequest_ReturnsOk() throws Exception {
    User mockUser = mock(User.class);

    ResultActions response = mockMvc.perform(post("/api/patient/tests/generate")
        .param("testAppointmentId", "1")
        .with(user(mockUser)));

    response.andExpect(status().isOk())
        .andExpect(jsonPath("$.status")
            .value(200))
        .andExpect(jsonPath("$.message")
            .value("Test result has been generated successfully"));
  }

  @Test
  public void generateTestResultForAppointment_InvalidRole_ReturnsForbidden() throws Exception {
    User mockUser = mock(User.class);

    doThrow(BadCredentialsForMedicalTest.class).when(medicalTestAppointmentService)
        .generateTestResultFormAppointment(any(Long.class));

    ResultActions response = mockMvc.perform(post("/api/patient/tests/generate")
        .param("testAppointmentId", "1")
        .with(user(mockUser)));

    response.andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status")
            .value(403))
        .andExpect(jsonPath("$.message")
            .value("Only admin can generate test results from test appointments"));
  }

  @Test
  public void generateTestResultForAppointment_AppointmentNotYetTaken_ReturnsBadRequest()
      throws Exception {
    User mockUser = mock(User.class);

    doThrow(InvalidMedicalTestDataException.class).when(medicalTestAppointmentService)
        .generateTestResultFormAppointment(any(Long.class));

    ResultActions response = mockMvc.perform(post("/api/patient/tests/generate")
        .param("testAppointmentId", "-1")
        .with(user(mockUser)));
    response.andExpect(status().isBadRequest());
  }
}
