package com.jesua.registration.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class BasePublicEntity extends BaseCreatedEntity {

    @Id
    @GeneratedValue()
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false)
    private UUID id;

}
