package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.request.CreateMedicalTestAppointmentRequest;
import com.ventionteams.medfast.dto.response.MedicalTestAppointmentResponse;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.MedicalTestCategory;
import  com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.medicaltest.InvalidMedicalTestDataException;
import com.ventionteams.medfast.mapper.MedicalTestsToResponse;
import com.ventionteams.medfast.pdf.TestAppointmentPdfGenerator;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the medical test service functionality with unit tests.
 */

@ExtendWith(MockitoExtension.class)
public class MedicalTestAppointmentServiceTest {

  @InjectMocks
  private MedicalTestAppointmentService medicalTestAppointmentService;

  @Mock
  private TestAppointmentPdfGenerator testAppointmentPdfGenerator;

  @Mock
  private MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  @Mock
  private MedicalTestsToResponse medicalTestsToResponse;

  @Mock
  private UserService userService;

  @Test
  public void saveMedicalTest_PastDateNoneDoctor_InvalidTestData_ExceptionThrown() {

    User admin = new User();
    admin.setRole(Role.ADMIN);
    CreateMedicalTestAppointmentRequest request = new CreateMedicalTestAppointmentRequest();
    request.setDoctorEmail(null);
    request.setPatientEmail("test@example.com");
    request.setDateOfTest(LocalDate.now().minusDays(1));

    assertThrows(InvalidMedicalTestDataException.class, () ->
        medicalTestAppointmentService.saveMedicalTestAppointment(request));
  }

  @Test
  public void saveMedicalTest_ValidRequest_SavesMedicalTest() {

    User admin = new User();
    admin.setRole(Role.ADMIN);
    User patient = new User();
    patient.setEmail("test@example.com");
    User doctor = new User();
    doctor.setRole(Role.DOCTOR);
    doctor.setEmail("doctor@example.com");
    CreateMedicalTestAppointmentRequest request = new CreateMedicalTestAppointmentRequest();
    request.setDoctorEmail("doctor@example.com");
    request.setPatientEmail("patient@example.com");
    request.setDateOfTest(LocalDate.now().minusDays(1));

    when(userService.getUserByEmailAndRole("doctor@example.com", Role.DOCTOR))
        .thenReturn(doctor);
    when(userService.getUserByEmailAndRole("patient@example.com", Role.PATIENT))
        .thenReturn(patient);

    medicalTestAppointmentService.saveMedicalTestAppointment(request);
    verify(medicalTestAppointmentRepository).save(any(MedicalTestAppointment.class));
  }


  @Test
  public void getMedicalTest_PastType_ReturnsList() {

    User admin = new User();
    admin.setRole(Role.ADMIN);
    User patient = new User();
    Optional<Integer> amount = Optional.of(0);
    AppointmentRequestType type = AppointmentRequestType.PAST;
    List<MedicalTestAppointment> medicalTestAppointments = List.of(
        MedicalTestAppointment.builder()
            .dateOfTest(LocalDate.now().minusDays(1))
            .build()
    );
    when(medicalTestAppointmentRepository.findAllByPatientOrderByDateOfTestDesc(patient))
        .thenReturn(medicalTestAppointments);

    List<MedicalTestAppointmentResponse> medicalTestAppointmentResponse
        = medicalTestAppointmentService
        .getMedicalTests(patient, amount, type);

    Assertions.assertThat(medicalTestAppointmentResponse).isEqualTo(
        medicalTestsToResponse.apply(medicalTestAppointments)
    );
  }

  @Test
  void generateTestResultForAppointment_AppointmentNotFound_ExceptionThrown() {
    when(medicalTestAppointmentRepository.findById(1L)).thenReturn(Optional.empty());
    User admin = new User();
    admin.setRole(Role.ADMIN);
    assertThrows(InvalidMedicalTestDataException.class, () ->
        medicalTestAppointmentService.generateTestResultFormAppointment(1L));

  }

  @Test
  void generateTestResultForAppointment_GeneratesTestResult() {

    MedicalTestAppointment appointment = new MedicalTestAppointment();
    appointment.setId(1L);
    appointment.setDoctor(new User());
    appointment.setPatient(new User());
    appointment.setDateOfTest(LocalDate.now());
    when(medicalTestAppointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
    User admin = new User();
    admin.setRole(Role.ADMIN);

    medicalTestAppointmentService.generateTestResultFormAppointment(1L);
    verify(testAppointmentPdfGenerator).generateAndSaveTestPdf(any(MedicalTestAppointment.class));
  }

  @Test
  void getPdfName_ValidInput_ReturnsPdfName() {
    MedicalTestAppointment medicalTestAppointment = new MedicalTestAppointment();
    medicalTestAppointment.setTestCategory(MedicalTestCategory.BLOOD);
    LocalDate date = LocalDate.now();
    medicalTestAppointment.setDateOfTest(date);
    User patient = new User();
    Person person = new Person();
    person.setName("John");
    person.setSurname("Doe");
    patient.setPerson(person);
    medicalTestAppointment.setPatient(patient);
    medicalTestAppointment.setId(1L);

    when(medicalTestAppointmentRepository.findById(1L))
        .thenReturn(Optional.of(medicalTestAppointment));
    String pdfName = medicalTestAppointmentService.getPdfName(medicalTestAppointment.getId());
    assertEquals("John_Doe_" + date + ".pdf", pdfName);
  }

}
