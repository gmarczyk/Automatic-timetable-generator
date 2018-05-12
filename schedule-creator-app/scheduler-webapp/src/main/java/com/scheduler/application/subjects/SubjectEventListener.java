package com.scheduler.application.subjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.domain.subjects.events.SubjectCreatedEvent;

@Configurable
public class SubjectEventListener {

    @Autowired
    private SubjectRepository subjectRepository;

    public void handle(final SubjectCreatedEvent subjectCreatedEvent) {
        subjectRepository.create(subjectCreatedEvent);
    }
}
