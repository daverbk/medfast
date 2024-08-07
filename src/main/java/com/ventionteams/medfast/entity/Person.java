package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Person entity class.
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "persons", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "surname", nullable = false)
  private String surname;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "sex")
  private String sex;

  @Column(name = "citizenship")
  private String citizenship;

  @Column(name = "street_address")
  private String streetAddress;

  @Column(name = "house")
  private String house;

  @Column(name = "apartment")
  private String apartment;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "zip")
  private String zip;

  @Column(name = "phone")
  private String phone;

  @OneToOne(mappedBy = "person")
  private User user;
}
