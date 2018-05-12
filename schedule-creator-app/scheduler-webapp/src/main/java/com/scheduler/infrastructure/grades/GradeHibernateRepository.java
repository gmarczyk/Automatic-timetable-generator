package com.scheduler.infrastructure.grades;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.events.GradeCreatedEvent;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;

@Repository
public class GradeHibernateRepository implements GradeRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<Grade> tenantAwareHibernateRepository;

    @Override
    public void create(final GradeCreatedEvent gradeCreatedEvent) {
        Grade grade = new Grade(gradeCreatedEvent.gradeSymbol);
        this.tenantAwareHibernateRepository.save(grade);
    }

    @Override
    public List<Grade> allGrades() {
        return this.tenantAwareHibernateRepository.list(Grade.class);
    }

    @Override
    public void delete(final Grade grade) {
        this.tenantAwareHibernateRepository.delete(grade);
    }
}
