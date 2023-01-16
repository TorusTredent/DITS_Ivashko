package com.example.dits.aspects;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.example.dits.controllers.AdminEditorController.addUser(..))")
    public void addUser() {

    }

    @Pointcut("execution(* com.example.dits.controllers.AdminTestController.addTopic(..))")
    public void addTopic() {
    }

    @Pointcut("execution(* com.example.dits.controllers.AdminTestController.addTest(..))")
    public void addTest() {
    }
}
