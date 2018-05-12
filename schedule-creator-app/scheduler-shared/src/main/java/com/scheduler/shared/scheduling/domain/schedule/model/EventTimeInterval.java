package com.scheduler.shared.scheduling.domain.schedule.model;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class EventTimeInterval {

    public String day;
    public String hours;

    public EventTimeInterval() {
        //empty
    }

    public EventTimeInterval(final String day, final String hours) {
        this.day = day;
        this.hours = hours;
    }

    public EventTimeInterval(EventTimeInterval interval) {
        this.day = interval.day == null ? null :new String(interval.day);
        this.hours = interval.hours == null ? null : new String(interval.hours);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EventTimeInterval that = (EventTimeInterval) o;

        return new EqualsBuilder().append(day, that.day).append(hours, that.hours).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(day).append(hours).toHashCode();
    }
}
