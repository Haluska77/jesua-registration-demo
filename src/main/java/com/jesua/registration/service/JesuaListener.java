package com.jesua.registration.service;

import com.jesua.registration.config.AppConfig;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JesuaListener implements ApplicationListener<ApplicationReadyEvent> {

    private FollowerService followerService;
    private CourseService courseService;
    private EmailServiceImpl emailService;
    private MessageBuilder messageBuilder;
    private AppConfig appConfig;

    @Value("${jesua.course.days.notification}")
    int notificationDays;

    @Autowired
    public JesuaListener(FollowerService followerService, CourseService courseService, EmailServiceImpl emailService, MessageBuilder messageBuilder, AppConfig appConfig) {
        this.followerService = followerService;
        this.courseService = courseService;
        this.emailService = emailService;
        this.messageBuilder = messageBuilder;
        this.appConfig = appConfig;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    }

    @Scheduled(cron = "0 0 18 * * *") // run every day at 18:00
    public void runCron() {

//        Course event = courseService.getCourse(appConfig.getActive());
//
//        LocalDate eventStartDate = event.getStartDate()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();

//        if (eventStartDate.compareTo(LocalDate.now()) == notificationDays) { // 1 day before event send notification
//            System.out.println("Send notification email");
//            jesuaUserService.getActiveUsersByEventId(event)
//                    .stream().limit(appConfig.getMax())
//                    .forEach(cs -> emailService.sendMessage(jesuaMessageBuilder.buildNotificationMessage(cs)));
//        }
    }
}
