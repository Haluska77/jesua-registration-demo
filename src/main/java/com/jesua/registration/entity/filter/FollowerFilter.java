package com.jesua.registration.entity.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FollowerFilter {

    private String token;
    private List<String> projects;
}
