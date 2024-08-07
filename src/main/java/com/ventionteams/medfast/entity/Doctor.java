package com.ventionteams.medfast.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Doctor entity class.
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "doctors", schema = "public")
public class Doctor extends Person {

  @Column(name = "license_number", nullable = false)
  private String licenseNumber;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @ManyToMany
  @JoinTable(
      name = "doctors_specializations_bridge",
      joinColumns = @JoinColumn(name = "doctor_id"),
      inverseJoinColumns = @JoinColumn(name = "specializations_id"))
  private List<Specialization> specializations;

  @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
  private List<ConsultationAppointment> consultationAppointments;
}
