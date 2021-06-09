package com.jesua.registration.entity.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProjectFilter {

    private String name;
    private UUID userId;
}
