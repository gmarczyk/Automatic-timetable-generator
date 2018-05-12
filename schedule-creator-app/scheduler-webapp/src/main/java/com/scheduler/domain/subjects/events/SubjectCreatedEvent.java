package com.scheduler.domain.subjects.events;

import org.apache.commons.lang3.Validate;

import com.scheduler.shared.event.domain.event.Event;

public class SubjectCreatedEvent implements Event {

    public final String name;

    public SubjectCreatedEvent(final String name) {
        Validate.notBlank(name);
        this.name = name;
    }
}
