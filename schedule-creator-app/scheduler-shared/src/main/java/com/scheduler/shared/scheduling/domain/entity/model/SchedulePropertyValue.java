package com.scheduler.shared.scheduling.domain.entity.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class SchedulePropertyValue implements Serializable /*implements ValueObject<String>*/ {

    private String value;

    public SchedulePropertyValue(String value) {
        this.value = value;
    }

    public SchedulePropertyValue() {
        // HIBERNATE
    }

    //@Override
    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SchedulePropertyValue that = (SchedulePropertyValue) o;

        return new EqualsBuilder().append(value, that.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).toHashCode();
    }
}
