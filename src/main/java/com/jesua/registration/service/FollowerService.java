package com.jesua.registration.service;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.event.FollowerCreatedEvent;
import com.jesua.registration.message.Message;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.jesua.registration.util.AppUtil.generateToken;
import static com.jesua.registration.util.AppUtil.instantToString;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final MessageBuilder messageBuilder;
    private final CourseService courseService;
    private final ApplicationEventPublisher eventPublisher;

    public List<Follower> getAllFollowersByEventId(int courseId) {

        return followerRepository.findByCourse(courseService.getCourse(courseId));
    }

    public Follower unsubscribeFollower(Follower follower) {

        follower.setUnregistered(Instant.now());
        follower.setAccepted(false);
        return followerRepository.save(follower);
    }

    public Follower acceptFollower(Follower follower, Course course) {

        follower.setAccepted(true);
        return followerRepository.save(follower);
    }

    @Transactional
    public FollowerResponseDto unsubscribe(String token, int courseId) {
        // accepted: active = 1
        // waiting: active = 0, unsubscribed = null
        // declined: active = 0, unsubscribed = NOT null

        //get event that user is applied
        Course event = courseService.getCourse(courseId);

        //get all users
        List<Follower> allFollowersByEventId = getAllFollowersByEventId(courseId);

        return allFollowersByEventId.stream().filter(
                m -> m.getToken().equals(token))
                .findFirst()
                .map(currentFollower -> {
                            if (currentFollower.getUnregistered() == null) {

                                if (currentFollower.isAccepted()) {
                                    //accept waiting follower and send email to confirm acceptance
                                    getFirstWaitingFollower(allFollowersByEventId)
                                            .ifPresent(waitingFollower -> {
                                                        acceptFollower(waitingFollower, event);
                                                        eventPublisher.publishEvent(new FollowerCreatedEvent(waitingFollower,
                                                                messageBuilder.buildSubstituteMessage(waitingFollower, event)));
                                                    }
                                            );

                                }

                                //unsubscribe current user
                                unsubscribeFollower(currentFollower);

                                return followerResponseDto(currentFollower, "You have been successfully unsubscribed");
                            } else {
                                return followerResponseDto(currentFollower, "You have already been unsubscribed on " + instantToString(currentFollower.getUnregistered()));
                            }
                        }
                )
                .orElseThrow(() -> new NoSuchElementException("User not found !!!"));

    }

    private Optional<Follower> getFirstWaitingFollower(List<Follower> allUsersByEventId) {
        return allUsersByEventId.stream()
                .sorted(Comparator.comparing(Follower::getRegistered))
                .filter(d -> !d.isAccepted() && d.getUnregistered() == null)
                .findFirst();
    }

    @Transactional
    public FollowerResponseDto addFollower(FollowerDto followerDto) {

        Follower follower = mapFollowerFromDto(followerDto);

        //Get number of active followers before new subscriber
        List<Follower> allFollowersByEventId = getAllFollowersByEventId(followerDto.getEventId());
        long acceptedFollowers = allFollowersByEventId.stream().filter(Follower::isAccepted).count();
        long waitingFollowers = allFollowersByEventId.stream().filter(b -> !b.isAccepted() && b.getUnregistered() == null).count();

        Course currentCourse = courseService.getCourse(followerDto.getEventId());
        Message emailMessage;
        String responseMessage = "Vaša registrácia na kurz Ješua (" + currentCourse.getDescription() + ", " + instantToString(currentCourse.getStartDate()) + ") prebehla úspešne! ";
        //build emailMessage
        if (acceptedFollowers < currentCourse.getCapacity()) {
            follower.setAccepted(true);
            emailMessage = messageBuilder.buildSuccessMessage(follower, currentCourse);
            responseMessage += "Tešíme sa na vašu účasť. Vidíme sa na stretnutí.";
        } else {
            follower.setAccepted(false);
            emailMessage = messageBuilder.buildUnsuccessMessage(follower, currentCourse);
            responseMessage +=
                    "<br> Momentálne je kapacita kurzu už naplnená. Ste v poradí. " +
                            "<br> Pred vami sa ešte prihlásilo <strong>" + waitingFollowers + "</strong> ľudí. " +
                            "<br> V prípade, že sa niektorý z účastníkov odhlási, dáme vám vedieť emailom na vašu adresu <strong>" + followerDto.getEmail() + "</strong";
        }

        Follower savedFollower = followerRepository.save(follower);
        eventPublisher.publishEvent(new FollowerCreatedEvent(savedFollower, emailMessage));
        //insert user into DB
        return followerResponseDto(savedFollower, responseMessage);
    }

    private Follower mapFollowerFromDto(FollowerDto followerDto) {
        Follower follower = new Follower();
        follower.setName(followerDto.getName());
        follower.setEmail(followerDto.getEmail());
        follower.setToken(generateToken());
        follower.setRegistered(Instant.now());
        follower.setCourse(courseService.getCourse(followerDto.getEventId()));

        return follower;
    }

    private FollowerResponseDto followerResponseDto(Follower follower, String message) {
        FollowerResponseDto followerResponseDto = new FollowerResponseDto();
        followerResponseDto.setFollower(new FollowerResponseDto.FollowerResponse(follower.getId(), follower.isAccepted()));
        followerResponseDto.setMessage(message);
        return followerResponseDto;
    }

    public List<Follower> getAllFollowers() {
        return followerRepository.findAll();
    }

    public Map<Integer, Map<Boolean, Long>> getAllFollowersByActiveEvents() {
        List<Follower> followerByOpenEvent = followerRepository.findFollowerByOpenEvent();

        return followerByOpenEvent.stream().filter(m->m.getUnregistered()==null)
                .collect(groupingBy(f-> f.getCourse().getId(),
                        groupingBy(Follower::isAccepted,
                                counting())
                        )
                );

    }
}
