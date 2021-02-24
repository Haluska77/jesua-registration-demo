package com.jesua.registration.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Setter
public class Stats {

    private long active;
    private long waiting;
}
