package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private int id;

    @NotNull
    @Size(max = 100)
    private String description;

    @NotNull
    @Column(name = "start_date")
    private Instant startDate;

    @NotNull
    private Boolean open;

}
