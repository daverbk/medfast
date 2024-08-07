package com.ventionteams.medfast.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Patient entity class.
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "patients", schema = "public")
public class Patient extends Person {

  @Column(name = "checkbox_terms_and_conditions", nullable = false)
  private boolean checkboxTermsAndConditions;

  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
  private List<ConsultationAppointment> consultationAppointments;
}
