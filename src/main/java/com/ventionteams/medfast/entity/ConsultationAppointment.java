package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import com.ventionteams.medfast.enums.AppointmentStatus;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Appointment entity class.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "consultation_appointments", schema = "public")
public class ConsultationAppointment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "doctor_id", nullable = false)
  private Doctor doctor;

  @ManyToOne
  @JoinColumn(name = "patient_id", nullable = false)
  private Patient patient;

  // TODO: add reference to Service after MED-106
  @Column(name = "service_id", nullable = false)
  private Long serviceId;

  @Column(name = "date_from", nullable = false)
  private LocalDateTime dateFrom;

  @Column(name = "date_to", nullable = false)
  private LocalDateTime dateTo;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "type", nullable = false)
  private String type;

  @Enumerated(EnumType.STRING)
  @Column(name = "appointment_status", nullable = false)
  private AppointmentStatus status;
}
