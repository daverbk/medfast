package com.ventionteams.medfast.dto.response;

import com.ventionteams.medfast.enums.MedicalTestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response with a medical test.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with a medical test")
public class MedicalTestAppointmentResponse {
  @Schema(description = "Medical test id", example = "3")
  private long id;

  @Schema(description = "Test name", example = "Blood test")
  private String testName;

  @Schema(description = "Doctor’s id", example = "3")
  private long doctorsId;

  @Schema(description = "Doctor’s name", example = "John Doe")
  private String doctorsName;

  @Schema(description = "Test category", example = "BLOOD")
  private MedicalTestCategory testCategory;

  @Schema(description = "Date of test in yyyy-mm-dd", example = "2021-07-09")
  private LocalDate dateOfTest;

  @Schema(description = "Tells if test appointment has pdf result", example = "true")
  private Boolean hasPdfResult;
}
