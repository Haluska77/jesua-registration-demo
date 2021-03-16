package com.jesua.registration.service;

import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
@Slf4j
@RequiredArgsConstructor
public class JesuaListener {

    private final FollowerService followerService;
    private final CourseRepository courseRepository;

    @Value("${jesua.course.days.notification}")
    int notificationDays;

    @Scheduled(cron = "${jesua.course.cron.expression}") // run every day at 8:00
//    @Scheduled(cron = "*/10 * * * * *") // run every day at 18:00
    public void runCron() {

        Instant fromDate = Instant.now();
        Instant toDate = LocalDate.ofInstant(fromDate, ZoneId.of("UTC"))
                .plusDays(notificationDays).atStartOfDay().toInstant(ZoneOffset.UTC);

        courseRepository.findByStartDateBetween(fromDate, toDate).forEach(followerService::sendNotificationEmail);
    }
}
