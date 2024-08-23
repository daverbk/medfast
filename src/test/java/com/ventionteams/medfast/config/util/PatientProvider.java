package com.ventionteams.medfast.config.util;

import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.repository.PatientRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides a patient entity for further integration testing. Please use the
 * {@link #getRawPassword(String)} method to get the raw password of the patient.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class PatientProvider implements EntityProvider<User> {

  private final Map<String, String> userRawPasswords = new HashMap<>();

  private final UserRepository userRepository;
  private final PatientRepository patientRepository;
  private final PasswordEncoder passwordEncoder;
  private final Faker faker;

  @Override
  @Transactional
  public User provide() {
    String password = faker
        .internet()
        .password(10, 50, true,
            true, true);

    User user = User.builder()
        .email(faker.internet().emailAddress())
        .password(passwordEncoder.encode(password))
        .role(Role.PATIENT)
        .enabled(true)
        .build();

    Patient patient = patientRepository.save(Patient.builder()
        .checkboxTermsAndConditions(false)
        .birthDate(faker.timeAndDate().birthday())
        .name(faker.name().firstName())
        .surname(faker.name().lastName())
        .streetAddress(faker.address().streetAddress())
        .house(faker.address().streetAddressNumber())
        .apartment(faker.address().buildingNumber())
        .city(faker.address().city())
        .state(faker.address().city())
        .zip(faker.address().zipCode())
        .phone(faker.phoneNumber().subscriberNumber(11))
        .sex(faker.demographic().sex())
        .citizenship(faker.country().name())
        .user(user)
        .build());

    user.setPerson(patient);
    User savedUser = userRepository.save(user);
    userRawPasswords.put(savedUser.getEmail(), password);

    return savedUser;
  }

  public String getRawPassword(String email) {
    return userRawPasswords.get(email);
  }
}
