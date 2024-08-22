package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.CreateMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.response.MedicalTestAppointmentResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.exception.medicaltest.BadCredentialsForMedicalTest;
import com.ventionteams.medfast.exception.medicaltest.InvalidMedicalTestDataException;
import com.ventionteams.medfast.service.MedicalTestAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Medical tests controller that handles the test requests.
 */
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Medical test controller", description = "Operations related to medical tests")
@RequestMapping("/api/patient/tests")
public class MedicalTestAppointmentController {
  private final MedicalTestAppointmentService medicalTestAppointmentService;

  /**
   * Provides test creation. Accessible only for admin and doctor.
   */
  @Operation(summary = "create test without pdf result")
  @PostMapping("/create")
  public ResponseEntity<StandardizedResponse<MedicalTestAppointmentResponse>> saveTest(
      @AuthenticationPrincipal
      @RequestBody @Valid CreateMedicalTestAppointmentRequest request) {
    StandardizedResponse<MedicalTestAppointmentResponse> response;
    try {

      MedicalTestAppointmentResponse medicalTestAppointmentresponse = medicalTestAppointmentService
          .saveMedicalTestAppointment(request);
      response = StandardizedResponse.ok(
          medicalTestAppointmentresponse,
          HttpStatus.OK.value(),
          "Test has been created successfully");
    }  catch (InvalidMedicalTestDataException | UsernameNotFoundException e) {
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Saving test failed.",
          e.getClass().getName(),
          e.getMessage());
    }
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides the tests for the logged-in user.
   */
  @Operation(summary = "Request the list of patient's tests")
  @GetMapping
  public ResponseEntity<StandardizedResponse<List<MedicalTestAppointmentResponse>>> getSortedTests(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "type") AppointmentRequestType type) {
    StandardizedResponse<List<MedicalTestAppointmentResponse>> response;
    try {
      List<MedicalTestAppointmentResponse> medicalTestsAppointments =
          medicalTestAppointmentService.getMedicalTests(user, amount, type);
      response = StandardizedResponse.ok(
          medicalTestsAppointments,
          HttpStatus.OK.value(),
          "Operation successful");
    } catch (InvalidMedicalTestDataException e) {
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid request",
          e.getClass().getName(),
          e.getMessage()
      );
    }
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  /**
   * Provides PDF result of test and generates it if it doesn't exist.
   * Accessible for the logged-in user.
   */
  @Operation(summary = "get test result")
  @GetMapping("/result")
  public ResponseEntity<Object> getTestResult(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "testId") Long testId) {
    try {
      byte[] pdf = medicalTestAppointmentService.getTestResult(user, testId);
      String pdfName = medicalTestAppointmentService.getPdfName(testId);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", pdfName);
      headers.setContentLength(pdf.length);
      return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    } catch (Exception e) {
      StandardizedResponse<String> response;
      if (e instanceof InvalidMedicalTestDataException) {
        response = StandardizedResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request",
            e.getClass().getName(),
            e.getMessage());
      } else if (e instanceof BadCredentialsForMedicalTest) {
        response = StandardizedResponse.error(
            HttpStatus.FORBIDDEN.value(),
            "Generating test failed.",
            e.getClass().getName(),
            e.getMessage());
      } else {
        response = StandardizedResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Generating test failed.",
            e.getClass().getName(),
            e.getMessage());
      }
      return ResponseEntity.status(response.getStatus()).body(response);
    }
  }

  /**
   * Generates PDF test Result for the appointment. Accessible only for admin.
   */
  @Operation(summary = "generate test result pdf for appointment")
  @PostMapping("/generate")
  public ResponseEntity<StandardizedResponse<String>> generateTestResultForAppointment(
      @AuthenticationPrincipal
      @RequestParam Long testAppointmentId) {
    StandardizedResponse<String> response;
    try {
      medicalTestAppointmentService.generateTestResultFormAppointment(testAppointmentId);
      response = StandardizedResponse.ok(
          "Test result has been generated successfully",
          HttpStatus.OK.value(),
          "Test result has been generated successfully");
    }  catch (Exception e) {
      if (e instanceof BadCredentialsForMedicalTest) {
        response = StandardizedResponse.error(
            HttpStatus.FORBIDDEN.value(),
            "Only admin can generate test results from test appointments",
            e.getClass().getName(),
            e.getMessage());
      } else if (e instanceof InvalidMedicalTestDataException) {
        response = StandardizedResponse.error(
            HttpStatus.BAD_REQUEST.value(),
            "generating test result failed.",
            e.getClass().getName(),
            e.getMessage());
      } else {
        response = StandardizedResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Saving test failed.",
            e.getClass().getName(),
            e.getMessage());
      }
    }
    return ResponseEntity.status(response.getStatus()).body(response);
  }

}