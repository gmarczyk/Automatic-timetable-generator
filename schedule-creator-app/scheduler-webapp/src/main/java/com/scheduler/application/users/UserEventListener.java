package com.scheduler.application.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.application.LoginService;
import com.scheduler.shared.event.domain.event.Handler;
import com.scheduler.shared.users.domain.users.UserRegisteredEvent;

@Configurable
public class UserEventListener extends Handler {

    @Autowired
    private LoginService loginService;

    public void handle(UserRegisteredEvent userRegisteredEvent) {
        loginService.registerNewUser(userRegisteredEvent);
    }
}
