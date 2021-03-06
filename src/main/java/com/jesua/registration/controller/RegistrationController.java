package com.jesua.registration.controller;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.filter.FollowerFilter;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.FollowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("registration/")
@RequiredArgsConstructor
public class RegistrationController {

    private final FollowerService followerService;

    @PostMapping("add")
    public SuccessResponse<FollowerEntityResponseDto> addFollower(@Valid @RequestBody FollowerDto followerDto) {

        FollowerResponseDto followerResponseDto = followerService.addFollower(followerDto);

        return new SuccessResponse<>(followerResponseDto.getFollower(), followerResponseDto.getMessage());
    }

    @GetMapping("")
    public List<FollowerEntityResponseDto> getAllFollowersBy(FollowerFilter followerFilter) {

        return followerService.getAllFollowersBy(followerFilter);
    }

    @GetMapping("token/{id}")
    public FollowerEntityResponseDto getFollowerByToken(@PathVariable String id) {

        return followerService.getFollowerByToken(id);
    }

    // must be 'GET' to call it from web
    @GetMapping("unsubscribe")
    public SuccessResponse<FollowerEntityResponseDto> unsubscribeFollower(
            @RequestParam("token") String token,
            @RequestParam("event") long eventId) {

        FollowerResponseDto followerResponseDto = followerService.unsubscribe(token, eventId);

        return new SuccessResponse<>(followerResponseDto.getFollower(), followerResponseDto.getMessage());

    }
}
