package com.scheduler.domain.subjects;

import java.util.List;

import com.scheduler.domain.subjects.events.SubjectCreatedEvent;

public interface SubjectRepository {

    List<Subject> allSubjects();

    void delete(Subject subject);

    void create(SubjectCreatedEvent subjectCreatedEvent);
}
