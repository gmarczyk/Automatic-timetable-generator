package com.scheduler.domain.schedule.events;

import com.scheduler.shared.event.domain.event.Event;

public class ScheduleEventUpdatedEvent implements Event {

    public ScheduleEventCreatedEvent command;
    public long id;

    public ScheduleEventUpdatedEvent(final ScheduleEventCreatedEvent command, final long id) {
        this.command = command;
        this.id = id;
    }
}
