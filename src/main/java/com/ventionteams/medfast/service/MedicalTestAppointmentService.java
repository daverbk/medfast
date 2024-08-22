package com.ventionteams.medfast.service;

import com.ventionteams.medfast.dto.request.CreateMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.response.MedicalTestAppointmentResponse;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.medicaltest.BadCredentialsForMedicalTest;
import com.ventionteams.medfast.exception.medicaltest.InvalidMedicalTestDataException;
import com.ventionteams.medfast.mapper.MedicalTestsToResponse;
import com.ventionteams.medfast.pdf.TestAppointmentPdfGenerator;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Medical test service responsible operations related to medical tests.
 */
@Service
@RequiredArgsConstructor
public class MedicalTestAppointmentService {
  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;
  private final UserService userService;
  private final MedicalTestsToResponse medicalTestsToResponse;
  private final TestAppointmentPdfGenerator testAppointmentPdfGenerator;

  /**
   * Creates test for given user.
   */

  @Transactional
  public MedicalTestAppointmentResponse saveMedicalTestAppointment(
                                        CreateMedicalTestAppointmentRequest request) {

    LocalDate dateOfTest = request.getDateOfTest();
    LocalDate now = LocalDate.now();
    if (request.getDoctorEmail() == null && dateOfTest.isBefore(now)) {
      throw new InvalidMedicalTestDataException("Already taken tests "
          + "should have a doctor assigned to them");
    }
    User doctor = null;
    if (request.getDoctorEmail() != null) {
      doctor = userService.getUserByEmailAndRole(request.getDoctorEmail(), Role.DOCTOR);
    }

    User patient = userService.getUserByEmailAndRole(request.getPatientEmail(), Role.PATIENT);

    MedicalTestAppointment medicalTestAppointment = MedicalTestAppointment.builder()
            .pdf(null)
            .testName(request.getTestName())
            .patient(patient)
            .doctor(doctor)
            .testCategory(request.getTestCategory())
            .dateOfTest(request.getDateOfTest())
            .build();
    this.save(medicalTestAppointment);
    return medicalTestsToResponse.testAppointmentToResponse(medicalTestAppointment);
  }

  public void save(MedicalTestAppointment test) {
    medicalTestAppointmentRepository.save(test);
  }

  /**
   * Provides list of tests for given user.
   */
  public List<MedicalTestAppointmentResponse> getMedicalTests(User user,
                                                    Optional<Integer> amount,
                                                    AppointmentRequestType type) {
    int testAmount = amount.orElse(2);
    if (testAmount < 0) {
      throw new InvalidMedicalTestDataException("If amount param presented "
          + "then it must be positive or zero");
    }
    return getFilteredTests(user, type, testAmount);
  }

  private List<MedicalTestAppointmentResponse> getFilteredTests(User user,
                                                                AppointmentRequestType type,
                                                                int amount) {
    LocalDate now = LocalDate.now();
    List<MedicalTestAppointment> filteredTests =
        medicalTestAppointmentRepository.findAllByPatientOrderByDateOfTestDesc(user).stream()
        .filter(test -> switch (type) {
          case PAST -> test.getDateOfTest().isBefore(now);
          case UPCOMING -> test.getDateOfTest().isAfter(now);
        })
        .limit(amount > 0 ? amount : Long.MAX_VALUE)
        .collect(Collectors.toList());

    return medicalTestsToResponse.apply(filteredTests);
  }

  /**
   * Generates test result PDF for given test. Accessible only for admin.
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  public void generateTestResultFormAppointment(Long appointmentId) {
    generateTestResultFormAppointmentForScheduler(appointmentId);
  }

  /**
   * Generates test result PDF for given test. Used by job scheduler.
   */
  public void generateTestResultFormAppointmentForScheduler(Long testAppointmentId) {

    MedicalTestAppointment testAppointment =
        medicalTestAppointmentRepository.findById(testAppointmentId).orElseThrow(() ->
            new InvalidMedicalTestDataException("Test appointment not found"));
    if (testAppointment.getDateOfTest().isAfter(LocalDate.now())) {
      throw new InvalidMedicalTestDataException("Test appointment is not yet taken");
    }
    if (testAppointment.getPdf() == null) {
      testAppointmentPdfGenerator.generateAndSaveTestPdf(testAppointment);
    } else {
      throw new InvalidMedicalTestDataException("Test result already exists");
    }
  }

  /**
   * Provides test result PDF for given test.
   */
  public byte[] getTestResult(User user, Long testId) {
    MedicalTestAppointment medicalTestAppointment =
        medicalTestAppointmentRepository.findById(testId).orElseThrow(() ->
        new InvalidMedicalTestDataException("Test appointment not found"));

    if (!Objects.equals(medicalTestAppointment.getPatient().getId(), user.getId())
        && user.getRole().equals(Role.PATIENT)) {
      throw new BadCredentialsForMedicalTest(
          "Only admin, doctor and patient who owns the test can access test results");
    }
    if (medicalTestAppointment.getPdf() == null) {
      throw new InvalidMedicalTestDataException("Test result not found");
    }
    return medicalTestAppointment.getPdf();
  }

  /**
   * Provides name of PDF result for given test.
   */
  public String getPdfName(Long testId) {
    MedicalTestAppointment medicalTestAppointment =
        medicalTestAppointmentRepository.findById(testId).orElseThrow(() ->
        new InvalidMedicalTestDataException("Test appointment not found"));
    String patientName = medicalTestAppointment.getPatient().getPerson().getName().concat("_")
        .concat(medicalTestAppointment.getPatient().getPerson().getSurname());
    String dateOfTest = medicalTestAppointment.getDateOfTest().toString();
    return patientName + "_" + dateOfTest + ".pdf";
  }

  public List<MedicalTestAppointment> findTestAppointmentsByDate(LocalDate date) {
    return medicalTestAppointmentRepository.findTestAppointmentsByDate(date);
  }
}
