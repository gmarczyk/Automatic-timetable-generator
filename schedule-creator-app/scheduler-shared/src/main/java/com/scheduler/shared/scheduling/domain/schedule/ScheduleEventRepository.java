package com.scheduler.shared.scheduling.domain.schedule;

import java.util.List;

import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;

public interface ScheduleEventRepository {
    List<ScheduleEvent> allEvents();

    void delete(ScheduleEvent onlyElement);

    ScheduleEvent create(SchedulePropertyValue teacherVal, SchedulePropertyValue gradeVal,
            SchedulePropertyValue subjectVal, SchedulePropertyValue roomVal, String hour, String day);

    void update(ScheduleEvent ex);

    ScheduleEvent findById(long id);
}
