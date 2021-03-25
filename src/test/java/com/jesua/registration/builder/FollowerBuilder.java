package com.jesua.registration.builder;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Follower;

import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;

public class FollowerBuilder {

    private static final UUID USER_ID = UUID.randomUUID();

    public static FollowerDto buildFollowerDto(){

        FollowerDto followerDto = new FollowerDto();
        followerDto.setName("sancho");
        followerDto.setEmail("jesua@jesua.com");
        followerDto.setEventId(1);
        followerDto.setGdpr(true);
        followerDto.setDeviceDetail("Ipad Air, iOS 14 version");

        return followerDto;
    }

//    public static FollowerEntityResponseDto buildFollowerEntityResponseDto(Instant unregistered, boolean accepted){
//
//        FollowerEntityResponseDto followerResponseDto = new FollowerEntityResponseDto();
//        followerResponseDto.setName("sancho");
//        followerResponseDto.setEmail("jesua@jesua.com");
//        followerResponseDto.setRegistered(Instant.now());
//        followerResponseDto.setUnregistered(unregistered);
//        followerResponseDto.setCourse(buildCourseResponseDto());
//        followerResponseDto.setAccepted(accepted);
//        followerResponseDto.setGdpr(true);
//        followerResponseDto.setDeviceDetail("Ipad Air, iOS 14 version");
//
//        return followerResponseDto;
//    }

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
        follower.setToken("sdfsd521d3ASDF54d32df156DF3");
        follower.setCourse(buildSavedCourse(followerDto.getEventId(), USER_ID, 100));
        follower.setRegistered(Instant.now());
        follower.setGdpr(followerDto.isGdpr());
        follower.setDeviceDetail(followerDto.getDeviceDetail());

        return follower;
    }

    public static Follower buildFullFollower(UUID id, String token, Instant unregistered, boolean accepted){

        Follower follower = new Follower();
        follower.setId(id);
        follower.setName("sancho");
        follower.setEmail("jesua@jesua.com");
        follower.setToken(token);
        follower.setCourse(buildSavedCourse(1, USER_ID, 100));
        follower.setRegistered(Instant.now());
        follower.setUnregistered(unregistered);
        follower.setAccepted(accepted);
        follower.setGdpr(true);
        follower.setDeviceDetail("Ipad Air, iOS 14 version");

        return follower;
    }
}
