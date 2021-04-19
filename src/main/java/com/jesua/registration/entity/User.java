package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "jesua_user")
public class User extends BasePublicEntity {

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

    @OneToMany(mappedBy="user")
    private Set<PasswordToken> passwordTokens;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
