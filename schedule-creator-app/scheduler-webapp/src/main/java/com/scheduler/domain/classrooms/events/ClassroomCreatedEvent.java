package com.scheduler.domain.classrooms.events;

import org.apache.commons.lang3.Validate;

import com.scheduler.shared.event.domain.event.Event;

public class ClassroomCreatedEvent implements Event {

    public String classroomCode;

    public ClassroomCreatedEvent(final String classroomCode) {
        Validate.notBlank(classroomCode);
        this.classroomCode = classroomCode;
    }
}
