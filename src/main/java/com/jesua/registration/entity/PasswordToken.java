package com.jesua.registration.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "password_token")
public class PasswordToken extends BasePrivateEntity {

    @NotNull
    @Size(max = 50)
    @Column(updatable = false)
    private String token;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(updatable = false)
    private Instant expiration;

    @NotNull
    private boolean applied;
}
