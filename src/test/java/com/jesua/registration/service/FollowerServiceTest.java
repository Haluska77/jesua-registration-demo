package com.jesua.registration.service;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.mapper.FollowerMapper;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.FollowerRepository;
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
import static com.jesua.registration.util.AppUtil.instantToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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

    @Mock
    CourseService courseService;

    @InjectMocks
    FollowerService followerService;

    @Test
    void findFirstWaitingFollowerTest() {

        Follower acceptedFollower1 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false);
        Follower acceptedFollower2 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true);
        Follower acceptedFollower3 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false);

        Optional<Follower> firstWaitingFollower = followerService.getFirstWaitingFollower(List.of(acceptedFollower1, acceptedFollower2, acceptedFollower3));

        assertEquals(acceptedFollower1, firstWaitingFollower.get());
    }

    @Test
    void getAllFollowersByEventIdTest() {


        Follower acceptedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, true);

        when(followerRepository.findByCourse(any())).thenReturn(List.of(acceptedFollower));

        List<Follower> allFollowersByEventId = followerService.getAllFollowersByEventId(1);

        assertEquals(1, allFollowersByEventId.size());
    }

    @Test
    void unsubscribeFollowerTest() {

        Follower follower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, true);
        Follower expectedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, Instant.now(), false);

        doReturn(expectedFollower).when(followerRepository).save(follower);

        followerService.unsubscribeFollower(follower);

        assertThat(follower).usingRecursiveComparison().ignoringFields("unregistered", "course").isEqualTo(expectedFollower);
        assertNotNull(follower.getUnregistered());
        assertThat(follower.getUnregistered())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void acceptFollowerTest() {

        Follower follower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, false);
        Follower expectedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, true);

        doReturn(expectedFollower).when(followerRepository).save(follower);

        followerService.acceptFollower(follower);

        assertThat(follower).usingRecursiveComparison().ignoringFields("course").isEqualTo(expectedFollower);

    }

    @Test
    void unsubscribeSuccessTest() {

        boolean MY_ACCEPTED = false;
        String responseMessage = "You have been successfully unsubscribed";

        Course course = buildSavedCourse(1, USER_ID, 100);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", null, true);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, MY_ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        doReturn(course).when(courseService).getCourse(1);
        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourse(course);

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), 1);
        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotSuccessTest() {

        boolean MY_ACCEPTED = false;

        Course course = buildSavedCourse(1, USER_ID, 100);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false);
        String responseMessage = "You have already been unsubscribed on " + instantToString(myFollower.getUnregistered());
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, MY_ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        doReturn(course).when(courseService).getCourse(1);
        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourse(course);

        FollowerResponseDto actualUnsubscribe = followerService.unsubscribe(myFollower.getToken(), 1);
        assertThat(actualUnsubscribe).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void unsubscribeNotFoundUserTest() {

        Course course = buildSavedCourse(1, USER_ID, 100);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(), TOKEN, null, false);
        Follower myFollower = buildFullFollower(MY_FOLLOWER_ID,"45ssd521d3ASDF54d32df156DF3", Instant.now().plusSeconds(60), false);

        doReturn(course).when(courseService).getCourse(1);
        doReturn(List.of(existingFollower, myFollower)).when(followerRepository).findByCourse(course);

        assertThatThrownBy(() -> followerService.unsubscribe(NOT_FOUND_TOKEN, 1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found !!!");

    }

    @Test
    void addSuccessFollowerTest() {

        boolean ACCEPTED = true;

        Course course = buildSavedCourse(1, USER_ID, 100);
        String responseMessage = "Vaša registrácia na kurz Ješua (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") prebehla úspešne! Tešíme sa na vašu účasť. Vidíme sa na stretnutí.";

        FollowerDto followerDto = buildFollowerDto();
        Follower rawFollower = buildFollowerFromDto(followerDto);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED);
        Follower existingFollower = buildFullFollower(UUID.randomUUID(),TOKEN, null, true);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        //mapper
        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);

        doReturn(course).when(courseService).getCourse(1);
        doReturn(newSavedFollower).when(followerRepository).save(any());
        doReturn(List.of(existingFollower)).when(followerRepository).findByCourse(course);

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void addWaitingFollowerTest() {

        boolean ACCEPTED = false;

        Course course = buildSavedCourse(1, USER_ID, 2);
        String responseMessage = "Vaša registrácia na kurz Ješua (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") " +
                "prebehla úspešne! <br> Momentálne je kapacita kurzu už naplnená. Ste v poradí. <br> Pred vami sa ešte prihlásilo <strong>0</strong> ľudí. " +
                "<br> V prípade, že sa niektorý z účastníkov odhlási, dáme vám vedieť emailom na vašu adresu <strong>jesua@jesua.com</strong";

        FollowerDto followerDto = buildFollowerDto();
        Follower rawFollower = buildFollowerFromDto(followerDto);
        Follower newSavedFollower = buildFullFollower(MY_FOLLOWER_ID, TOKEN, null, ACCEPTED);
        Follower existingFollower1 = buildFullFollower(UUID.randomUUID(),TOKEN, null, true);
        Follower existingFollower2 = buildFullFollower(UUID.randomUUID(),TOKEN, null, true);
        FollowerResponseDto.FollowerResponse followerResponse = buildFollowerResponse(MY_FOLLOWER_ID, ACCEPTED);
        FollowerResponseDto followerResponseDto = buildFollowerResponseDto(responseMessage, followerResponse);

        //mapper
        doReturn(rawFollower).when(followerMapper).mapDtoToEntity(followerDto);

        doReturn(course).when(courseService).getCourse(1);
        doReturn(List.of(existingFollower1, existingFollower2)).when(followerRepository).findByCourse(course);
        doReturn(newSavedFollower).when(followerRepository).save(any());

        FollowerResponseDto actualResponseDto = followerService.addFollower(followerDto);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(followerResponseDto);
    }

    @Test
    void sendNotificationEmail() {
    }

    @Test
    void sendEmail() {
    }
}