package com.jesua.registration.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jesua.registration.oauth.AuthProvider;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "jesua_user")
@NaturalIdCache
public class User extends BasePublicEntity {

    @Size(max = 50)
    private String avatar;

    @NotNull
    @Size(max = 50)
    @Column(name = "user_name")
    private String userName;

    @NaturalId
    @NotNull
    @Size(max = 100)
    private String email;

    @Size(max = 100)
    private String password;

    @NotNull
    @Size(max = 50)
    private String role;

    @NotNull
    private Boolean active;

    @OneToMany(mappedBy="user")
    private Set<PasswordToken> passwordTokens;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_project",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Set<Project> projects = new HashSet<>();

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
}
