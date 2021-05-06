package com.jesua.registration.entity.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CourseFilter {

    private UUID userId;
    private Boolean open;
    private Instant startDate;
    private List<Long> projectList;
}
