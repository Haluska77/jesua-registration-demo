package com.jesua.registration.service;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.filter.FollowerFilter;
import com.jesua.registration.event.EmailSenderListener;
import com.jesua.registration.event.FollowerCreatedEvent;
import com.jesua.registration.mapper.FollowerMapper;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.Message;
import com.jesua.registration.message.NotificationMessage;
import com.jesua.registration.message.SubstituteMessage;
import com.jesua.registration.message.SuccessMessage;
import com.jesua.registration.message.UnsuccessMessage;
import com.jesua.registration.repository.FollowerRepository;
import com.jesua.registration.repository.FollowerSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jesua.registration.util.AppUtil.instantToString;

@RequiredArgsConstructor
@Service
@Slf4j
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FollowerMapper followerMapper;
    private final EmailSenderListener emailSenderListener;
    private final SuccessMessage successMessage;
    private final UnsuccessMessage unsuccessMessage;
    private final SubstituteMessage substituteMessage;
    private final NotificationMessage notificationMessage;

    public Follower unsubscribeFollower(Follower follower) {

        follower.setUnregistered(Instant.now());
        follower.setAccepted(false);
        return followerRepository.save(follower);
    }

    public Follower acceptFollower(Follower follower) {

        follower.setAccepted(true);
        return followerRepository.save(follower);
    }

    @Transactional
    public FollowerResponseDto unsubscribe(String token, long courseId) {
        // accepted: active = 1
        // waiting: active = 0, unsubscribed = null
        // declined: active = 0, unsubscribed = NOT null

        //get all users
        List<Follower> allFollowersByEventId = followerRepository.findByCourseId(courseId);

        return allFollowersByEventId.stream().filter(
                        m -> m.getToken().equals(token))
                .findFirst()
                .map(currentFollower -> {
                            if (currentFollower.getUnregistered() == null) {
                                if (currentFollower.isAccepted()) {
                                    //accept waiting follower and send email to confirm acceptance
                                    getFirstWaitingFollower(allFollowersByEventId)
                                            .ifPresent(waitingFollower -> {
                                                        acceptFollower(waitingFollower);
                                                        eventPublisher.publishEvent(new FollowerCreatedEvent(waitingFollower,
                                                                substituteMessage.buildMessage(waitingFollower)));
                                                    }
                                            );
                                }

                                //unsubscribe current user
                                unsubscribeFollower(currentFollower);

                                return followerResponseDto(currentFollower, "Bol si úspešne odhlásený");
                            } else {
                                return followerResponseDto(currentFollower, "Už si bol odhlásený " + instantToString(currentFollower.getUnregistered()));
                            }
                        }
                )
                .orElseThrow(() -> new NoSuchElementException("User not found !!!"));

    }

    public Optional<Follower> getFirstWaitingFollower(List<Follower> allUsersByEventId) {
        return allUsersByEventId.stream()
                .sorted(Comparator.comparing(Follower::getCreated))
                .filter(d -> !d.isAccepted() && d.getUnregistered() == null)
                .findFirst();
    }

    @Transactional
    public FollowerResponseDto addFollower(FollowerDto followerDto) {

        Follower follower = followerMapper.mapDtoToEntity(followerDto);

        if (follower.getCourse() == null) {
            throw new NoSuchElementException("Event not found for follower '" + followerDto.getName() + "'");
        }

        //Get number of active followers before new subscriber
        List<Follower> allFollowersByEventId = followerRepository.findByCourseId(followerDto.getEventId());
        long acceptedFollowers = allFollowersByEventId.stream().filter(Follower::isAccepted).count();
        long waitingFollowers = allFollowersByEventId.stream().filter(b -> !b.isAccepted() && b.getUnregistered() == null).count();

        Course currentCourse = follower.getCourse();
        Message emailMessage;
        String responseMessage = "Tvoja registrácia na akciu (" + currentCourse.getDescription() + ", " + instantToString(currentCourse.getStartDate()) + ") prebehla úspešne! ";
        //build emailMessage
        if (acceptedFollowers < currentCourse.getCapacity()) {
            follower.setAccepted(true);
            emailMessage = successMessage.buildMessage(follower);
            responseMessage += "Tešíme sa na tvoju účasť. Vidíme sa na stretnutí.";
        } else {
            follower.setAccepted(false);
            emailMessage = unsuccessMessage.buildMessage(follower);
            responseMessage +=
                    "<br> Momentálne je kapacita akcie už naplnená. Si v poradí. " +
                            "<br> Pred tebou sa ešte prihlásilo <strong>" + waitingFollowers + "</strong> ľudí. " +
                            "<br> V prípade, že sa niektorý z účastníkov odhlási, dáme ti vedieť emailom na tvoju adresu <strong>" + followerDto.getEmail() + "</strong";
        }

        Follower savedFollower = followerRepository.save(follower);
        eventPublisher.publishEvent(new FollowerCreatedEvent(savedFollower, emailMessage));
        //insert user into DB
        return followerResponseDto(savedFollower, responseMessage);
    }

    private FollowerResponseDto followerResponseDto(Follower follower, String message) {
        FollowerEntityResponseDto followerEntityResponseDto = followerMapper.mapEntityToDto(follower);
        FollowerResponseDto followerResponseDto = new FollowerResponseDto();
        followerResponseDto.setFollower(followerEntityResponseDto);
        followerResponseDto.setMessage(message);
        return followerResponseDto;
    }

    public List<FollowerEntityResponseDto> getAllFollowersBy(FollowerFilter followerFilter) {

        return followerRepository.findAll(new FollowerSpecification(followerFilter))
                .stream().map(followerMapper::mapEntityToDto).collect(Collectors.toList());

    }

    void sendNotificationEmail(Course course) {

        followerRepository.findByCourseId(course.getId()).stream()
                .filter(Follower::isAccepted)
                .forEach(follower -> {
                    Message emailMessage = notificationMessage.buildMessage(follower);
                    emailSenderListener.sendNotificationEmail(course.getDescription(), emailMessage);
                });
    }

    public FollowerEntityResponseDto getFollowerByToken(String id) {
        return followerRepository.findByToken(id)
                .map(followerMapper::mapEntityToDto)
                .orElseThrow(() -> new NoSuchElementException("Neplatný token !!!"));
    }
}
