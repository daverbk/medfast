package com.ventionteams.medfast.pdf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.MedicalTestCategory;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests the test result PDF generator functionality with unit test.
 */
public class TestAppointmentPdfGeneratorTest {

  @Test
  void generatePdf_ValidInput_ReturnsByteArray() {
    Person person = new Person();
    person.setSurname("Doe");
    person.setName("John");
    person.setSurname("Doe");
    LocalDate testDate = LocalDate.of(2024, 8, 12);
    LocalDate dateOfBirth = LocalDate.of(1980, 1, 1);
    person.setBirthDate(dateOfBirth);
    User user = new User();
    user.setPerson(person);
    MedicalTestAppointment testAppointment = MedicalTestAppointment.builder()
        .dateOfTest(testDate)
        .testCategory(MedicalTestCategory.BLOOD)
        .testName("Vitamin D")
        .patient(user)
        .doctor(user)
        .build();
    MedicalTestAppointmentRepository medicalTestAppointmentRepository
        = Mockito.mock(MedicalTestAppointmentRepository.class);
    TestAppointmentPdfGenerator testAppointmentPdfGenerator =
        new TestAppointmentPdfGenerator(medicalTestAppointmentRepository);

    testAppointmentPdfGenerator.generateAndSaveTestPdf(testAppointment);
    assertNotNull(testAppointment.getPdf(), "Generated PDF byte array should not be null");
    assertTrue(testAppointment.getPdf().length > 0, "Generated PDF byte array should not be empty");
  }

}
