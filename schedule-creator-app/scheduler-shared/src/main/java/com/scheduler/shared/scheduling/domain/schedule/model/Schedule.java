package com.scheduler.shared.scheduling.domain.schedule.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.scheduler.shared.core.structure.AggregateRoot;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;

@AggregateRoot
public class Schedule {

    private Set<ScheduleEvent> events = new HashSet<>();
    private Set<ScheduleCondition> conditions = new HashSet<>();
    private Set<EventTimeInterval> timeIntervals = new HashSet<>();

    private Map<ScheduleProperty, Integer> perEntityCapacity = new HashMap<>();


    public void addEvent(ScheduleEvent event) {
        validateTime(event);

        this.events.add(event);
        event.setGenerationStatus(ScheduleEvent.GenerationStatus.ASSIGNED);
        updateCapacity(event);

        //ScheduleUpdateEvent
    }

    public void addTimeInterval(final EventTimeInterval ti) {
        this.timeIntervals.add(ti);
    }

    public void addCondition(final ScheduleCondition cd) {
        this.conditions.add(cd);
    }

    private void updateCapacity(final ScheduleEvent event) {
        for (final ScheduleProperty entity : event.getScheduleProperties()) {
            if(entity == null || entity.entityValue() == null || entity.getSchedulePropertyValue().getValue() == null) {
                continue;
            }

            Integer capacity = perEntityCapacity.get(entity);
            if(capacity == null) {
                perEntityCapacity.put(entity, 1);
            }
            else {
                perEntityCapacity.put(entity, capacity+1);
            }
        };
    }

    private void validateTime(ScheduleEvent event) {
        if(event.getTimeInterval() == null || event.getTimeInterval().day == null || event.getTimeInterval().hours == null) {
            throw new RuntimeException("No time period, can't set in schedule");
        }
    }

    private boolean isTaken(final EventTimeInterval timeInterval) {
        for (final ScheduleEvent event : events) {
            if(event.getTimeInterval().equals(timeInterval)) {
                return true;
            }
        }
        return false;
    }

    public Set<ScheduleEvent> getEvents() {
        return events;
    }

    public Set<ScheduleCondition> getConditions() {
        return conditions;
    }

    public Set<EventTimeInterval> getTimeIntervals() {
        return timeIntervals;
    }

    public Map<ScheduleProperty, Integer> getPerEntityCapacity() {
        return perEntityCapacity;
    }


}
