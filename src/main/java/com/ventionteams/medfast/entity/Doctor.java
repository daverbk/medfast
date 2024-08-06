package com.ventionteams.medfast.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

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
