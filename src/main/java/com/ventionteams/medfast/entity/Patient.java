package com.ventionteams.medfast.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
