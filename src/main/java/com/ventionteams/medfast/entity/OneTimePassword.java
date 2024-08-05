package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.TokenEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "one_time_passwords", schema = "public")
public class OneTimePassword extends TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
