package com.jesua.registration.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table (uniqueConstraints = {
        @UniqueConstraint(name = "uk_project__short_name", columnNames = "short_name")
})
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Project extends BasePrivateEntity {

    @NotNull
    @Size(max = 100)
    @Column(name = "short_name", unique=true)
    private String shortName;

    @NotNull
    @Size(max = 250)
    private String description;

    @NotNull
    private boolean active;
}
