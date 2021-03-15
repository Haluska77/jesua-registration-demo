package com.jesua.registration.controller;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.FollowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("registration/")
public class RegistrationController {

    private final FollowerService followerService;

    public RegistrationController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("add")
    public SuccessResponse<FollowerResponseDto.FollowerResponse> addFollower(@RequestBody FollowerDto followerDto) {

        FollowerResponseDto followerResponseDto = followerService.addFollower(followerDto);

        if (followerResponseDto.getFollower() == null) {
            throw new NoSuchElementException(followerResponseDto.getMessage());
        }
        return new SuccessResponse<>(followerResponseDto.getFollower(), followerResponseDto.getMessage());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("")
    public List<FollowerEntityResponseDto> getAllFollowers() {

        return followerService.getAllFollowers();

    }

    @GetMapping("allFollowersByActiveEvents")
    public Map<Integer, Map<Boolean, Long>> getFollowersByActiveEvent() {

        //get all registered for event
        return followerService.getAllFollowersByActiveEvents();

    }

    // must be 'GET' to call it from web
    @GetMapping("unsubscribe")
    public SuccessResponse<FollowerResponseDto.FollowerResponse> unsubscribeFollower(
            @RequestParam("token") String token,
            @RequestParam("event") int eventId) {

        FollowerResponseDto followerResponseDto = followerService.unsubscribe(token, eventId);

        return new SuccessResponse<>(followerResponseDto.getFollower(), followerResponseDto.getMessage());

    }
}
