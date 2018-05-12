package com.scheduler.shared.scheduling.domain.entity.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class ScheduleProperty {

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "if_name"))
    private SchedulePropertyName schedulePropertyName;

    @Embedded
    private SchedulePropertyValue schedulePropertyValue;

    private Boolean nonUnique = false;

    public ScheduleProperty() {
        // HIBERNATE
    }

    public ScheduleProperty(String name, String value) {
        this.schedulePropertyName = new SchedulePropertyName(name);
        this.schedulePropertyValue = new SchedulePropertyValue(value);
    }

    public ScheduleProperty(String name, String value, boolean nonUnique) {
        this.schedulePropertyName = new SchedulePropertyName(name);
        this.schedulePropertyValue = new SchedulePropertyValue(value);
        this.nonUnique = nonUnique;
    }

    public Boolean getNonUnique() {
        return nonUnique;
    }

    public SchedulePropertyName propertyName() {
        return schedulePropertyName;
    }

    public SchedulePropertyValue entityValue() {
        return schedulePropertyValue;// == null ? new SchedulePropertyValue(null) : schedulePropertyValue;
    }

    public void setSchedulePropertyValue(final SchedulePropertyValue schedulePropertyValue) {
        this.schedulePropertyValue = schedulePropertyValue;
    }

    public void describe() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return  schedulePropertyName.getName() + ": " + entityValue().getValue();
    }

    public SchedulePropertyName getSchedulePropertyName() {
        return schedulePropertyName;
    }

    public void setSchedulePropertyName(final SchedulePropertyName schedulePropertyName) {
        this.schedulePropertyName = schedulePropertyName;
    }

    public SchedulePropertyValue getSchedulePropertyValue() {
        return schedulePropertyValue == null ? new SchedulePropertyValue(null) : schedulePropertyValue;
    }

    public void setNonUnique(final Boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public void updateNonNull() {
        this.schedulePropertyValue = new SchedulePropertyValue(null);
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ScheduleProperty that = (ScheduleProperty) o;

        return new EqualsBuilder().append(schedulePropertyName, that.schedulePropertyName)
                .append(schedulePropertyValue, that.schedulePropertyValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(schedulePropertyName).append(schedulePropertyValue).toHashCode();
    }
}
