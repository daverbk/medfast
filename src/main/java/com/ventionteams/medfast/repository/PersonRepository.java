package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
