package com.scheduler.application.grades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.domain.grades.events.GradeCreatedEvent;
import com.scheduler.shared.event.domain.event.Handler;

@Configurable
public class GradeEventListener extends Handler {

    @Autowired
    private GradeRepository gradeRepository;

    public void handle(GradeCreatedEvent gradeCreatedEvent) {
        this.gradeRepository.create(gradeCreatedEvent);
    }
}
