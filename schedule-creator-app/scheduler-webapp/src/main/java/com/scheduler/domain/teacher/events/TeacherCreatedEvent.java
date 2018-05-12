package com.scheduler.domain.teacher.events;

import org.apache.commons.lang3.Validate;

import com.scheduler.shared.event.domain.event.Event;

public class TeacherCreatedEvent implements Event {

    public String firstName;
    public String lastName;

    public TeacherCreatedEvent(final String firstName, final String lastName) {
        Validate.notBlank(firstName);
        Validate.notBlank(lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
