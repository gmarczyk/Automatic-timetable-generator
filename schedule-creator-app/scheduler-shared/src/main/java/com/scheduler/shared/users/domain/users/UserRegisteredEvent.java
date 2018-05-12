package com.scheduler.shared.users.domain.users;

import org.apache.commons.lang3.Validate;

import com.scheduler.shared.event.domain.event.Event;

public class UserRegisteredEvent implements Event {

    public final String uname;
    public final String password;
    public final UserRole role;

    public UserRegisteredEvent(final String uname, final String password, final UserRole role) {
        Validate.notBlank(uname);
        Validate.notBlank(password);
        Validate.notNull(role);
        this.role =role;
        this.uname = uname;
        this.password = password;
    }
}
