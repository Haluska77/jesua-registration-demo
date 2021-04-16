package com.jesua.registration.service;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.FollowerMapper;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.FollowerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerResponse;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerResponseDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFullFollower;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static com.jesua.registration.util.AppUtil.instantToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    private static final UUID MY_FOLLOWER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    public static final String TOKEN = "sdfsd521d3ASDF54d32df156DF3";
    public static final String NOT_FOUND_TOKEN = "sF5t61dG63ASDF54d32df156DF3";

    @Mock
    MessageBuilder messageBuilder;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    EmailServiceImpl emailService;

    @Mock
    FollowerRepository followerRepository;

    @Mock
    FollowerMapper followerMapper;

    @InjectMocks
    FollowerService followerService;

    private static Course course;
    private static User user;
    private static Project project;

    @BeforeAll
    static void setUp(){
        project = buildProject(1);
        user = buildUserWithId(USER_ID, project);
        course = buildSavedCourse(1, user, 100, project);
    }

    @Test
    void findFirstWaitingFollowerTest() {

        Follower acceptedFollower1 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower acceptedFollower2 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course);
        Follower acceptedFollower3 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);

        Optional<Follower> firstWaitingFollower = followerService.getFirstWaitingFollower(List.of(acceptedFollower1, acceptedFollower2, acceptedFollower3));

        assertEquals(acceptedFollower1, firstWaitingFollower.get());
    }

    @Test
    void unsubscribeFollowerTest() {

        Follower follower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, true, course);
        Follower expectedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, Instant.now(), false, course);

        doReturn(expectedFollower).when(followerRepository).save(follower);

        followerService.unsubscribeFollower(follower);

        verify(followerRepository).save(follower);

        assertThat(follower).usingRecursiveComparison().ignoringFields("registered","unregistered", "course").isEqualTo(expectedFollower);
        assertNotNull(follower.getUnregistered());
        assertThat(follower.getRegistered()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
        assertThat(follower.getUnregistered()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void acceptFollowerTest() {

        Follower follower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, false, course);
        Follower expectedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, true, course);

        doReturn(expectedFollower).when(followerRepository).save(follower);

        followerService.acceptFollower(follower);

        verify(followerRepository).save(follower);

        assertThat(follower).usingRecursiveComparison().ignoringFields("course").isEqualTo(expectedFollower);

    }

    @Test
    void unsubscribeSuccessTest() {

        boolean MY_ACCEPTED = false;
        String responseMessage = "You have been successfully unsubscribed";


        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", null, true, course);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, MY_ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), course.getId());

        verify(followerRepository).findByCourseId(course.getId());

        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotSuccessTest() {

        boolean MY_ACCEPTED = false;

        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false, course);
        String responseMessage = "You have already been unsubscribed on " + instantToString(myFollower.getUnregistered());
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, MY_ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), course.getId());

        verify(followerRepository).findByCourseId(course.getId());

        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotFoundUserTest() {

        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false, course);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());

        assertThatThrownBy(() -> followerService.unsubscribe(NOT_FOUND_TOKEN, course.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found !!!");

    }

    @Test
    void addSuccessFollowerTest() {

        boolean ACCEPTED_TRUE = true;

        String responseMessage = "Vaša registrácia na kurz Ješua (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") prebehla úspešne! Tešíme sa na vašu účasť. Vidíme sa na stretnutí.";

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower rawFollower = buildFollowerFromDto(followerDto, course);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED_TRUE, course);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(),TOKEN, null, true, course);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, ACCEPTED_TRUE);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        //mapper
        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);

        doReturn(newSavedFollower).when(followerRepository).save(any());
        doReturn(List.of(existingFollower)).when(followerRepository).findByCourseId(course.getId());

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        verify(followerMapper).mapDtoToEntity(followerDto);

        verify(followerRepository).save(any());
        verify(followerRepository).findByCourseId(course.getId());

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void addWaitingFollowerTest() {

        boolean ACCEPTED_FALSE = false;

        Course course2 = buildSavedCourse(2, user, 2, project);
        String responseMessage = "Vaša registrácia na kurz Ješua (" + course2.getDescription() + ", " + instantToString(course2.getStartDate()) + ") " +
                "prebehla úspešne! <br> Momentálne je kapacita kurzu už naplnená. Ste v poradí. <br> Pred vami sa ešte prihlásilo <strong>0</strong> ľudí. " +
                "<br> V prípade, že sa niektorý z účastníkov odhlási, dáme vám vedieť emailom na vašu adresu <strong>jesua@jesua.com</strong";

        FollowerDto followerDto = buildFollowerDto(course2.getId());
        Follower rawFollower = buildFollowerFromDto(followerDto, course2);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED_FALSE, course2);
        Follower existingFollower1 = buildFullFollower(UUID.randomUUID(),TOKEN, null, true, course2);
        Follower existingFollower2 = buildFullFollower(UUID.randomUUID(),TOKEN, null, true, course2);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, ACCEPTED_FALSE);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        //mapper
        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);

        doReturn(List.of(existingFollower1, existingFollower2)).when(followerRepository).findByCourseId(course2.getId());
        doReturn(newSavedFollower).when(followerRepository).save(any());

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        verify(followerMapper).mapDtoToEntity(followerDto);
        verify(followerRepository).findByCourseId(course2.getId());
        verify(followerRepository).save(any());

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }
}