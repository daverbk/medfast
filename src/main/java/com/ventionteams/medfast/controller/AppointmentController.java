package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.response.AppointmentResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.exception.appointment.NegativeAppointmentsAmountException;
import com.ventionteams.medfast.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Appointment controller that handles the appointment requests.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Appointment Controller", description = "Operations related to appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;

  /**
   * Provides the appointments for the logged in patient.
   */
  @Operation(summary = "Request the list of patients' appointments")
  @GetMapping("/api/patient/appointments")
  public ResponseEntity<StandardizedResponse<List<AppointmentResponse>>> getAppointments(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "amount", required = false) Optional<Integer> amount,
      @RequestParam(name = "type") AppointmentRequestType type) {

    StandardizedResponse<List<AppointmentResponse>> response;

    try {
      Optional<Person> person = Optional.ofNullable(user.getPerson());

      List<AppointmentResponse> appointments =
          appointmentService.getAppointments(person, amount, type);

      response = StandardizedResponse.ok(
          appointments,
          HttpStatus.OK.value(),
          "Operation successful");
    } catch (NegativeAppointmentsAmountException ex) {
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid request",
          ex.getClass().getName(),
          ex.getMessage()
      );
    }

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
