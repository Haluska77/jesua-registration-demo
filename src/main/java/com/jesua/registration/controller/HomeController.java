package com.jesua.registration.controller;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.dto.Stats;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.FollowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final FollowerService followerService;

    @GetMapping("/")
    public Map<Integer, Stats> getStatistics() {

        //get all registered for event
        return followerService.getStatistics();

    }

}
