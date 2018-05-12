package com.scheduler.domain.schedule.events;

import com.scheduler.shared.event.domain.event.Event;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;

public class ScheduleEventCreatedEvent implements Event {

    public SchedulePropertyValue teacherVal;
    public SchedulePropertyValue gradeVal;
    public SchedulePropertyValue subjectVal;
    public SchedulePropertyValue roomVal;
    public String hour;
    public String day;

    public ScheduleEventCreatedEvent(final SchedulePropertyValue teacherVal, final SchedulePropertyValue gradeVal,
            final SchedulePropertyValue subjectVal, final SchedulePropertyValue roomVal, final String hour,
            final String day) {
        this.teacherVal = teacherVal;
        this.gradeVal = gradeVal;
        this.subjectVal = subjectVal;
        this.roomVal = roomVal;
        this.hour = hour;
        this.day = day;
    }
}
