package com.jesua.registration.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Project extends BasePrivateEntity {

    @NotNull
    @Size(max = 100)
    private String shortName;

    @NotNull
    @Size(max = 250)
    private String description;

    @NotNull
    private boolean active;

    @JsonBackReference
    @ManyToMany(mappedBy = "projects")
    private Set<User> users = new HashSet<>();

}
