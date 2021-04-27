package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public class BaseCreatedEntity {

    @Column(updatable = false)
    @CreationTimestamp
    private Instant created;
}
