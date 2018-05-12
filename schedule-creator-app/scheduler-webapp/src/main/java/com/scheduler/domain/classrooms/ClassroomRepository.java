package com.scheduler.domain.classrooms;

import java.util.List;

import com.scheduler.domain.classrooms.events.ClassroomCreatedEvent;

public interface ClassroomRepository {

    void create(ClassroomCreatedEvent classroomCreatedEvent);

    List<Classroom> allClassrooms();

    void delete(Classroom classroom);
}
