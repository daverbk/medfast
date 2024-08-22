package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import com.ventionteams.medfast.enums.MedicalTestCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Medical test entity class.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "test_appointments", schema = "public")
public class MedicalTestAppointment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_name", nullable = false)
  private String testName;

  @ManyToOne
  @JoinColumn(name = "patient_id", nullable = false)
  private User patient;

  @ManyToOne
  @JoinColumn(name = "doctor_id")
  private User doctor;

  @Enumerated(EnumType.STRING)
  @Column(name = "test_category", nullable = false)
  private MedicalTestCategory testCategory;


  @Column(name = "date_of_test", nullable = false)
  private LocalDate dateOfTest;

  @Column(name = "pdf_result")
  private byte[] pdf;

}
