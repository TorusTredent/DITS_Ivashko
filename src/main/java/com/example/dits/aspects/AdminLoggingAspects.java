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
        Object[] args = joinPoint.getArgs();
        UserInfoDTO user = (UserInfoDTO) args[0];
        String message = "New user (" + user.getLogin() + ") is created, time: " + LocalDateTime.now();
        log.info(message);
    }

    @After("com.example.dits.aspects.Pointcuts.addTopic()")
    public void loggingAfterAddTopic(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        TopicDTO topic = (TopicDTO) args[0];
        String message = "New topic (" + topic.getTopicName() + ") is created, time: " + LocalDateTime.now();
        log.info(message);
    }

    @After("com.example.dits.aspects.Pointcuts.addTest()")
    public void loggingAfterAddTest(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        TestInfoDTO test = (TestInfoDTO) args[0];
        String message = "New test (" + test.getName() + ") was created, time: " + LocalDateTime.now();
        log.info(message);
    }
}