package com.jesua.registration.service;

import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.mapper.FollowerMapper;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.NotificationMessage;
import com.jesua.registration.message.SubstituteMessage;
import com.jesua.registration.message.SuccessMessage;
import com.jesua.registration.message.UnsuccessMessage;
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

import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerEntityResponseDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerResponseDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFullFollower;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static com.jesua.registration.util.AppUtil.instantToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    SuccessMessage successMessage;
    @Mock
    UnsuccessMessage unsuccessMessage;
    @Mock
    SubstituteMessage substituteMessage;
    @Mock
    NotificationMessage notificationMessage;

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
    static void setUp() {
        project = buildProject(1);
        user = buildUserWithId(USER_ID);
        course = buildSavedCourse(1, user, 100, project);
    }

    @Test
    void findFirstWaitingFollowerTest() {

        Follower acceptedFollower1 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        acceptedFollower1.setCreated(Instant.now());
        Follower acceptedFollower2 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course);
        acceptedFollower2.setCreated(Instant.now().plusMillis(100));
        Follower acceptedFollower3 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        acceptedFollower3.setCreated(Instant.now().plusMillis(200));

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

        assertThat(follower).usingRecursiveComparison().ignoringFields("unregistered").isEqualTo(expectedFollower);
        assertThat(follower.getUnregistered()).isCloseTo(expectedFollower.getUnregistered(), within(1, ChronoUnit.SECONDS));
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
        String responseMessage = "Bol si úspešne odhlásený";

        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        existingFollower.setCreated(Instant.now());
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID, "45ssd521d3ASDF54d32df156DF3", null, true, course);
        myFollower.setCreated(Instant.now());
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course);
        FollowerEntityResponseDto followerEntityResponseDto = buildFollowerEntityResponseDto(myFollower, courseResponseDto);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerEntityResponseDto);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());
        doReturn(followerEntityResponseDto).when(followerMapper).mapEntityToDto(myFollower);

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), course.getId());

        verify(followerRepository).findByCourseId(course.getId());
        verify(followerMapper).mapEntityToDto(myFollower);

        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotSuccessTest() {

        boolean MY_ACCEPTED = false;

        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID, "45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false, course);
        String responseMessage = "Už si bol odhlásený " + instantToString(myFollower.getUnregistered());

        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course);
        FollowerEntityResponseDto followerEntityResponseDto = buildFollowerEntityResponseDto(myFollower, courseResponseDto);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerEntityResponseDto);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());
        doReturn(followerEntityResponseDto).when(followerMapper).mapEntityToDto(myFollower);

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), course.getId());

        verify(followerRepository).findByCourseId(course.getId());
        verify(followerMapper).mapEntityToDto(myFollower);

        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotFoundUserTest() {

        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID, "45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false, course);

        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourseId(course.getId());

        assertThatThrownBy(() -> followerService.unsubscribe(NOT_FOUND_TOKEN, course.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found !!!");

    }

    @Test
    void addSuccessFollowerTest() {

        boolean ACCEPTED_TRUE = true;

        String responseMessage = "Tvoja registrácia na akciu (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") prebehla úspešne! Tešíme sa na tvoju účasť. Vidíme sa na stretnutí.";

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower rawFollower = buildFollowerFromDto(followerDto, course);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED_TRUE, course);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course);

        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course);
        FollowerEntityResponseDto followerEntityResponseDto = buildFollowerEntityResponseDto(newSavedFollower, courseResponseDto);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerEntityResponseDto);

        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);
        doReturn(newSavedFollower).when(followerRepository).save(any());
        doReturn(List.of(existingFollower)).when(followerRepository).findByCourseId(course.getId());
        doReturn(followerEntityResponseDto).when(followerMapper).mapEntityToDto(newSavedFollower);

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        verify(followerMapper).mapDtoToEntity(followerDto);
        verify(followerRepository).save(any());
        verify(followerRepository).findByCourseId(course.getId());
        verify(followerMapper).mapEntityToDto(newSavedFollower);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void addFollowerCourseNotFoundTest() {

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower rawFollower = buildFollowerFromDto(followerDto, course);
        rawFollower.setCourse(null);

        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);

        assertThatThrownBy(() -> followerService.addFollower(followerDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Event not found for follower '" + followerDto.getName() + "'");
    }

    @Test
    void addWaitingFollowerTest() {

        boolean ACCEPTED_FALSE = false;

        Course course2 = buildSavedCourse(2, user, 2, project);
        String responseMessage = "Tvoja registrácia na akciu (" + course2.getDescription() + ", " + instantToString(course2.getStartDate()) + ") " +
                "prebehla úspešne! <br> Momentálne je kapacita akcie už naplnená. Si v poradí. <br> Pred tebou sa ešte prihlásilo <strong>0</strong> ľudí. " +
                "<br> V prípade, že sa niektorý z účastníkov odhlási, dáme ti vedieť emailom na tvoju adresu <strong>jesua@jesua.com</strong";

        FollowerDto followerDto = buildFollowerDto(course2.getId());
        Follower rawFollower = buildFollowerFromDto(followerDto, course2);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED_FALSE, course2);
        Follower existingFollower1 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course2);
        Follower existingFollower2 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course2);

        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course);
        FollowerEntityResponseDto followerEntityResponseDto = buildFollowerEntityResponseDto(newSavedFollower, courseResponseDto);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerEntityResponseDto);

        //mapper
        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);
        doReturn(List.of(existingFollower1, existingFollower2)).when(followerRepository).findByCourseId(course2.getId());
        doReturn(newSavedFollower).when(followerRepository).save(any());
        doReturn(followerEntityResponseDto).when(followerMapper).mapEntityToDto(newSavedFollower);

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        verify(followerMapper).mapDtoToEntity(followerDto);
        verify(followerRepository).findByCourseId(course2.getId());
        verify(followerRepository).save(any());
        verify(followerMapper).mapEntityToDto(newSavedFollower);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void getFollowerByTokenNotFoundTest() {

        Follower acceptedFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course);

        doReturn(Optional.of(acceptedFollower)).when(followerRepository).findByToken(TOKEN);
        doReturn(null).when(followerMapper).mapEntityToDto(acceptedFollower);

        assertThatThrownBy(() -> followerService.getFollowerByToken(TOKEN))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Neplatný token !!!");
    }

}