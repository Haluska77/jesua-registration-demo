package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public class BaseCreatedEntity {

    @Column(updatable = false)
    private Instant created;

    @PrePersist
    private void onCreate() {
        created = Instant.now();
    }
}
