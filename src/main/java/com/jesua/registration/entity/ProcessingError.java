package com.jesua.registration.entity;

import com.jesua.registration.oauth.AuthProvider;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class ProcessingError extends BasePrivateEntity {

    @NotNull
    @Column(name = "error_type")
    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @NotNull
    @Size(max = 250)
    @Column(name = "text")
    private String text;
}
