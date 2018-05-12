package com.scheduler.domain.grades.events;

import org.apache.commons.lang3.Validate;

import com.scheduler.shared.event.domain.event.Event;

public class GradeCreatedEvent implements Event {

    public String gradeSymbol;

    public GradeCreatedEvent(final String gradeSymbol) {
        Validate.notBlank(gradeSymbol);
        this.gradeSymbol = gradeSymbol;
    }
}
