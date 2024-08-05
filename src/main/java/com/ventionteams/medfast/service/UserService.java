package com.ventionteams.medfast.service;

import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDetailsService getUserDetailsService() {
        return this::getUserByEmail;
    }

    public User getUserByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

    @Transactional
    public User create(SignUpRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail(), "User with this email already exists");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .role(Role.PATIENT)
            .enabled(false)
            .birthDate(request.getBirthDate())
            .name(request.getName())
            .surname(request.getSurname())
            .streetAddress(request.getStreetAddress())
            .house(request.getHouse())
            .apartment(request.getApartment())
            .city(request.getCity())
            .state(request.getState())
            .zip(request.getZip())
            .phone(request.getPhone())
            .sex(request.getSex())
            .citizenship(request.getCitizenship())
            .build();

        return save(user);
    }

    @Transactional
    public void resetPassword(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        save(user);
    }

    public User save(User user) {
        return repository.save(user);
    }
}
