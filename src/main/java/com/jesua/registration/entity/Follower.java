package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Follower {
//TODO investigate Serializable
    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false)
    private UUID id;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private com.jesua.registration.entity.Course course;

    @NotNull
    @Column(updatable = false)
    private Instant registered;

    private Instant unregistered;

    @NotNull
    private boolean accepted;

}
