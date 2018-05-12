package com.scheduler.application.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.domain.teacher.events.TeacherCreatedEvent;
import com.scheduler.shared.event.domain.event.Handler;

@Configurable
public class TeacherEventListener extends Handler {

    @Autowired
    private TeacherService teacherService;

    public void handle(final TeacherCreatedEvent event) {
        teacherService.create(event);
    }
}
