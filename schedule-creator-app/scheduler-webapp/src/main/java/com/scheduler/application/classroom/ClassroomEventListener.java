package com.scheduler.application.classroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.domain.classrooms.events.ClassroomCreatedEvent;
import com.scheduler.shared.event.domain.event.Handler;

@Configurable
public class ClassroomEventListener extends Handler {

    @Autowired
    private ClassroomRepository classroomRepository;

    public void handle(final ClassroomCreatedEvent classroomCreatedEvent) {
        classroomRepository.create(classroomCreatedEvent);
    }

}
