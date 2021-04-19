package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Course extends BasePrivateEntity{

    @NotNull
    @Size(max = 100)
    private String description;

    @NotNull
    @Column(name = "start_date")
    private Instant startDate;

    @NotNull
    private Boolean open;

    @NotNull
    private int capacity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User user;

    @Size(max = 100)
    private String image;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
