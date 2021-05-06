package com.jesua.registration.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Project extends BasePrivateEntity {

    @NotNull
    @Size(max = 100)
    private String shortName;

    @NotNull
    @Size(max = 250)
    private String description;

    @NotNull
    private boolean active;
}
