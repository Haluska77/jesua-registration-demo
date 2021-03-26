package com.jesua.registration.builder;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;

import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;

public class FollowerBuilder {

    private static final UUID USER_ID = UUID.randomUUID();
    public static final String NAME = "sancho";
    public static final String EMAIL = "jesua@jesua.com";
    public static final String TOKEN = "sdfsd521d3ASDF54d32df156DF3";
    public static final String DEVICE = "Ipad Air, iOS 14 version";

    public static FollowerDto buildFollowerDto(){

        FollowerDto followerDto = new FollowerDto();
        followerDto.setName(NAME);
        followerDto.setEmail(EMAIL);
        followerDto.setEventId(1);
        followerDto.setGdpr(true);
        followerDto.setDeviceDetail(DEVICE);

        return followerDto;
    }

    public static FollowerResponseDto.FollowerResponse buildFollowerResponse(UUID id, boolean accepted ) {

        return new FollowerResponseDto.FollowerResponse(id, accepted);

    }

    public static FollowerResponseDto buildFollowerResponseDto(String message, FollowerResponseDto.FollowerResponse response){

        FollowerResponseDto followerResponseDto = new FollowerResponseDto();
        followerResponseDto.setMessage(message);
        followerResponseDto.setFollower(response);

        return followerResponseDto;
    }

    //build Entity follower from input Dto
    public static Follower buildFollowerFromDto(FollowerDto followerDto){

        Follower follower = new Follower();
        follower.setName(followerDto.getName());
        follower.setEmail(followerDto.getEmail());
        follower.setToken(TOKEN);
        follower.setCourse(buildSavedCourse(followerDto.getEventId(), USER_ID, 100));
        follower.setRegistered(Instant.now());
        follower.setGdpr(followerDto.isGdpr());
        follower.setDeviceDetail(followerDto.getDeviceDetail());

        return follower;
    }

    public static Follower buildFullFollower(UUID id, String token, Instant unregistered, boolean accepted){

        Follower follower = new Follower();
        follower.setId(id);
        follower.setName(NAME);
        follower.setEmail(EMAIL);
        follower.setToken(token);
        follower.setCourse(buildSavedCourse(1, USER_ID, 100));
        follower.setRegistered(Instant.now());
        follower.setUnregistered(unregistered);
        follower.setAccepted(accepted);
        follower.setGdpr(true);
        follower.setDeviceDetail(DEVICE);

        return follower;
    }

    public static Follower buildFollowerByCourse(Course course){

        Follower follower = new Follower();
        follower.setName(NAME);
        follower.setEmail(EMAIL);
        follower.setToken(TOKEN);
        follower.setCourse(course);
        follower.setRegistered(Instant.now());
        follower.setUnregistered(null);
        follower.setAccepted(true);
        follower.setGdpr(true);
        follower.setDeviceDetail(DEVICE);

        return follower;
    }
}
