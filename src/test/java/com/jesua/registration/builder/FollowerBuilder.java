package com.jesua.registration.builder;

import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;

import java.time.Instant;
import java.util.UUID;

public class FollowerBuilder {

    public static final String NAME = "sancho";
    public static final String EMAIL = "jesua@jesua.com";
    public static final String TOKEN = "sdfsd521d3ASDF54d32df156DF3";
    public static final String DEVICE = "Ipad Air, iOS 14 version";

    public static FollowerDto buildFollowerDto(long courseId){

        FollowerDto followerDto = new FollowerDto();
        followerDto.setName(NAME);
        followerDto.setEmail(EMAIL);
        followerDto.setEventId(courseId);
        followerDto.setGdpr(true);
        followerDto.setDeviceDetail(DEVICE);

        return followerDto;
    }
//
//    public static FollowerResponseDto.FollowerResponse buildFollowerResponse(UUID id, boolean accepted ) {
//
//        return new FollowerResponseDto.FollowerResponse(id, accepted);
//
//    }

    public static FollowerResponseDto buildFollowerResponseDto(String message, FollowerEntityResponseDto followerEntityResponseDto){

        FollowerResponseDto followerResponseDto = new FollowerResponseDto();
        followerResponseDto.setMessage(message);
        followerResponseDto.setFollower(followerEntityResponseDto);

        return followerResponseDto;
    }

    //build Entity follower from input Dto
    public static Follower buildFollowerFromDto(FollowerDto followerDto, Course course){

        Follower follower = new Follower();
        follower.setName(followerDto.getName());
        follower.setEmail(followerDto.getEmail());
        follower.setToken(TOKEN);
        follower.setCourse(course);
        follower.setGdpr(followerDto.isGdpr());
        follower.setDeviceDetail(followerDto.getDeviceDetail());

        return follower;
    }

    public static Follower buildFullFollower(UUID id, String token, Instant unregistered, boolean accepted, Course course){

        Follower follower = new Follower();
        follower.setId(id);
        follower.setName(NAME);
        follower.setEmail(EMAIL);
        follower.setToken(token);
        follower.setCourse(course);
        follower.setUnregistered(unregistered);
        follower.setAccepted(accepted);
        follower.setGdpr(true);
        follower.setDeviceDetail(DEVICE);

        return follower;
    }

    public static FollowerEntityResponseDto buildFollowerEntityResponseDto(Follower follower, CourseResponseDto courseResponseDto){

        FollowerEntityResponseDto followerEntityResponseDto = new FollowerEntityResponseDto();
        followerEntityResponseDto.setId(follower.getId());
        followerEntityResponseDto.setName(follower.getName());
        followerEntityResponseDto.setEmail(follower.getEmail());
        followerEntityResponseDto.setToken(follower.getToken());
        followerEntityResponseDto.setCourse(courseResponseDto);
        followerEntityResponseDto.setCreated(follower.getCreated());
        followerEntityResponseDto.setUnregistered(follower.getUnregistered());
        followerEntityResponseDto.setAccepted(follower.isAccepted());
        followerEntityResponseDto.setGdpr(follower.isGdpr());
        followerEntityResponseDto.setDeviceDetail(follower.getDeviceDetail());

        return followerEntityResponseDto;
    }
}
