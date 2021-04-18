package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Follower extends BasePublicEntity {

    @NotBlank(message = "Email nemôže byť prázdny")
    @Email(message = "Zadajte platnú emailovú adresu")
    @NotNull
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Meno nemôže byť prázdne")
    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    private String token;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull
    @Column(updatable = false)
    private Instant registered;

    private Instant unregistered;

    @NotNull
    private boolean accepted;

    @NotNull
    private boolean gdpr;

    @Size(max = 250)
    @Column(name = "device_detail")
    private String deviceDetail;
}
