package com.scheduler.application.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.scheduler.domain.schedule.events.ScheduleEventCreatedAndAssignedEvent;
import com.scheduler.domain.schedule.events.ScheduleEventCreatedEvent;
import com.scheduler.domain.schedule.events.ScheduleEventUpdatedEvent;
import com.scheduler.shared.event.domain.event.Handler;

@Configurable
public class ScheduleEventEventListener extends Handler {

    @Autowired
    private ScheduleEventService scheduleService;

    public void handle(ScheduleEventCreatedAndAssignedEvent event) {
        scheduleService.createAndAssign(event.command);
    }

    public void handle(ScheduleEventUpdatedEvent event) {
        scheduleService.update(event.command, event.id);
    }

    public void handle(ScheduleEventCreatedEvent event) {
        scheduleService.create(event);
    }
}
