package com.scheduler.domain.grades;

import java.util.List;

import com.scheduler.domain.grades.events.GradeCreatedEvent;

public interface GradeRepository {

    public void create(final GradeCreatedEvent gradeCreatedEvent);

    public List<Grade> allGrades();

    public void delete(final Grade grade);

}
