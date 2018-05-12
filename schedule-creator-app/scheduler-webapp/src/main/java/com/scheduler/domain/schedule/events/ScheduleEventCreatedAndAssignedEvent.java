package com.scheduler.domain.schedule.events;

import com.scheduler.shared.event.domain.event.Event;

public class ScheduleEventCreatedAndAssignedEvent implements Event {

    public ScheduleEventCreatedEvent command;

    public ScheduleEventCreatedAndAssignedEvent(final ScheduleEventCreatedEvent command) {
        this.command = command;
    }
}
