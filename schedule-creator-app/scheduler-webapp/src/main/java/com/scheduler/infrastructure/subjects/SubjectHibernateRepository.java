package com.scheduler.infrastructure.subjects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.events.SubjectCreatedEvent;
import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;

@Repository
public class SubjectHibernateRepository implements SubjectRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<Subject> tenantAwareHibernateRepository;

    @Override
    public List<Subject> allSubjects() {
        return tenantAwareHibernateRepository.list(Subject.class);
    }

    @Override
    public void delete(final Subject subject) {
        this.tenantAwareHibernateRepository.delete(subject);
    }

    @Override
    public void create(final SubjectCreatedEvent subjectCreatedEvent) {
        this.tenantAwareHibernateRepository.save(new Subject(subjectCreatedEvent.name));
    }

}
