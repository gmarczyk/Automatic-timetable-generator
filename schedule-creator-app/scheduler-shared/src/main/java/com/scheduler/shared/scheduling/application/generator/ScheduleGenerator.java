package com.scheduler.shared.scheduling.application.generator;

import java.util.List;
import java.util.Set;

import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;

public interface ScheduleGenerator {

    Schedule generate(Set<EventTimeInterval> timeIntervals, List<ScheduleEvent> events,
            Set<ScheduleCondition> conditions);
}
