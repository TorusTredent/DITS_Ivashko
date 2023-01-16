package com.example.dits.aspects;

import com.example.dits.dto.TestInfoDTO;
import com.example.dits.dto.TopicDTO;
import com.example.dits.dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AdminLoggingAspects {

    @After("com.example.dits.aspects.Pointcuts.addUser()")
    public void loggingAfterAddUser(JoinPoint joinPoint) {
        UserInfoDTO user = (UserInfoDTO) joinPoint.getArgs()[0];
        log.info(getMessage("user", user.getLogin()));
    }

    @After("com.example.dits.aspects.Pointcuts.addTopic()")
    public void loggingAfterAddTopic(JoinPoint joinPoint) {
        TopicDTO topic = (TopicDTO) joinPoint.getArgs()[0];
        log.info(getMessage("topic", topic.getTopicName()));
    }

    @After("com.example.dits.aspects.Pointcuts.addTest()")
    public void loggingAfterAddTest(JoinPoint joinPoint) {
        TestInfoDTO test = (TestInfoDTO) joinPoint.getArgs()[0];
        log.info(getMessage("test", test.getName()));
    }


    private String getMessage(String action, String name) {
        return "New " + action + " (" + name + ") was created, time: " + LocalDateTime.now();
    }
}