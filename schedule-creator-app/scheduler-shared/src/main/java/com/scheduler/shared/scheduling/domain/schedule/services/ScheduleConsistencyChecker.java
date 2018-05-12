package com.scheduler.shared.scheduling.domain.schedule.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.DomainService;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@DomainService
@Component
public class ScheduleConsistencyChecker {

    public boolean canBeSetInSchedule(EventProposal event, Schedule schedule) {

        if(!event.hasFullTimeSpecified()) {
            throw new RuntimeException("No time specified");
        }

        for (final ScheduleProperty scheduleProperty : event.getProperties()) {
            if(scheduleProperty.getNonUnique()) {
                continue;
            }

            if (scheduleProperty.entityValue().getValue() != null && anyEventHasThisEntityUsedOnThatTime(
                    scheduleProperty,
                    event.getTimeInterval(), new ArrayList<>(schedule.getEvents()))) {
                return false;
            }
        }

        return true;
    }

    public boolean canBeSetInSchedule(EventProposal event, List<ScheduleEvent> events) {

        if(!event.hasFullTimeSpecified()) {
            throw new RuntimeException("No time specified");
        }

        for (final ScheduleProperty scheduleProperty : event.getProperties()) {
            if(scheduleProperty.getNonUnique()) {
                continue;
            }

            if (scheduleProperty.entityValue().getValue() != null && anyEventHasThisEntityUsedOnThatTime(
                    scheduleProperty,
                    event.getTimeInterval(), events)) {
                return false;
            }
        }

        return true;
    }

    // TODO will not work for periods of time, need to create nachodzaNaSiebie()
    // TODO need to check if schedule supports such intervals
    private boolean anyEventHasThisEntityUsedOnThatTime(final ScheduleProperty entity,
            final EventTimeInterval timeInterval, final List<ScheduleEvent> events) {

        for (final ScheduleEvent event : events) {

            if (event.getScheduleProperties().contains(entity) && event.getTimeInterval().day.equals(timeInterval.day)
                    && event.getTimeInterval().hours.equals(timeInterval.hours)) {
                return true;
            }
        }

        return false;
    }
}
