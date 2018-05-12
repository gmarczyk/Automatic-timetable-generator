package com.scheduler.infrastructure.classrooms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scheduler.domain.classrooms.Classroom;
import com.scheduler.domain.classrooms.events.ClassroomCreatedEvent;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;

@Repository
public class ClassroomHibernateRepository implements ClassroomRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<Classroom> tenantAwareHibernateRepository;

    @Override
    public void create(final ClassroomCreatedEvent classroomCreatedEvent) {
        Classroom classroom = new Classroom(classroomCreatedEvent.classroomCode);
        this.tenantAwareHibernateRepository.save(classroom);
    }

    @Override
    public List<Classroom> allClassrooms() {
        return this.tenantAwareHibernateRepository.list(Classroom.class);
    }

    @Override
    public void delete(final Classroom classroom) {
        this.tenantAwareHibernateRepository.delete(classroom);
    }
}
