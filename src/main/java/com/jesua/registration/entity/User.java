package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "jesua_user")
public class User {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false)
    private UUID id;

    @Size(max = 50)
    private String avatar;

    @NotNull
    @Size(max = 50)
    @Column(name = "user_name")
    private String userName;

    @NotNull
    @Size(max = 100)
    private String email;

    @NotNull
    @Size(max = 100)
    private String password;

    @NotNull
    @Size(max = 50)
    private String role;

    @NotNull
    private Boolean active;

    @Column(updatable = false)
    private Instant created;

    @OneToMany(mappedBy="user")
    private Set<PasswordToken> passwordTokens;
}
