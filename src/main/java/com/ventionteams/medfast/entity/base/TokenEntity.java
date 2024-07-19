package com.ventionteams.medfast.entity.base;

import com.ventionteams.medfast.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class TokenEntity extends BaseEntity {
    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
