package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
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
@Table(name = "locations", schema = "public")
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(name = "house", nullable = false)
    private String house;

    @OneToMany(mappedBy = "location")
    private List<ConsultationAppointment> consultationAppointments;

    @OneToMany(mappedBy = "location")
    private List<Doctor> doctors;
}
