package com.ventionteams.medfast.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import com.ventionteams.medfast.repository.PatientRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Checks user service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @Mock
  private UserRepository repository;

  @Mock
  private PatientRepository patientRepository;

  @InjectMocks
  private UserService service;

  @Test
  public void getUserByEmail_InvalidEmail_ExceptionThrown() {
    String email = "invalid@example.com";

    when(repository.findByEmail(email)).thenReturn(Optional.empty());

    Assertions.assertThrows(UsernameNotFoundException.class,
        () -> service.getUserByEmail(email));
  }

  @Test
  public void getUserByEmail_ValidEmail_ReturnsUser() {
    User user = User.builder().id(3L).build();
    String email = "valid@example.com";

    when(repository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

    User returnedUser = service.getUserByEmail(email);

    Assertions.assertEquals(returnedUser, user);
  }

  @Test
  public void create_UserExist_ExceptionThrown() {
    SignUpRequest request = mock(SignUpRequest.class);

    when(repository.existsByEmail(request.getEmail())).thenReturn(true);

    Assertions.assertThrows(UserAlreadyExistsException.class,
        () -> service.create(request));
  }

  @Test
  public void create_UserNotExist_CreateAndReturnUser() {
    SignUpRequest request = new SignUpRequest("user@example.com", "passwrod", "John",
        "Doe", LocalDate.now(), "Main street", "123", "42 a", "Chicago",
        "Illinios", "60007", "12345678900", "male", "Canada");

    when(repository.existsByEmail(request.getEmail())).thenReturn(false);

    when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(patientRepository.save(any(Patient.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    User createdUser = service.create(request);

    Assertions.assertNotNull(createdUser);
    Assertions.assertEquals(request.getEmail(), createdUser.getEmail());
    Assertions.assertEquals(request.getPassword(), createdUser.getPassword());
    Assertions.assertEquals(Role.PATIENT, createdUser.getRole());
    Assertions.assertFalse(createdUser.isEnabled());

    Person createdPerson = createdUser.getPerson();
    Assertions.assertNotNull(createdPerson);
    Assertions.assertEquals(request.getName(), createdPerson.getName());
    Assertions.assertEquals(request.getSurname(), createdPerson.getSurname());
    Assertions.assertEquals(request.getBirthDate(), createdPerson.getBirthDate());
  }

  @Test
  public void resetPassword_ValidUser_UpdateUserPassword() {
    User user = User.builder().email("user@example.com").build();
    String encodedPassword = "encodedPassword";

    when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.resetPassword(user, encodedPassword);

    Assertions.assertEquals(encodedPassword, user.getPassword());
    verify(repository, times(1)).save(user);
  }
}
