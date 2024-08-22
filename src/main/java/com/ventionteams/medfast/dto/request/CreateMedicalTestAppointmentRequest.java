package com.ventionteams.medfast.dto.request;

import com.ventionteams.medfast.enums.MedicalTestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create medical test request transfer object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create test request")
public class CreateMedicalTestAppointmentRequest {

  @Schema(description = "Test name", example = "Blood test")
  @NotBlank(message = "Test name must not be blank")
  private String testName;

  @Schema(description = "patient's Email", example = "johndoe@gmail.com")
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Email must follow the format user@example.com")
  private String patientEmail;

  @Schema(description = "doctor's Email", example = "johndoe@gmail.com")
  @Email(message = "Email must follow the format user@example.com")
  private String doctorEmail;

  @Schema(description = "Test category", example = "BLOOD")
  @NotNull(message = "Test category must not be blank")
  private MedicalTestCategory testCategory;

  @Schema(description = "Date of test in yyyy-mm-dd", example = "2021-07-09")
  @NotNull(message = "Date of test must not be blank")
  private LocalDate dateOfTest;
}
