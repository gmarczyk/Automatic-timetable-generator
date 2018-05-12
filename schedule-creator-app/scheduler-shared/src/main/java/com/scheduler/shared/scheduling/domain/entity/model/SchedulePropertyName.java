package com.scheduler.shared.scheduling.domain.entity.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
@Embeddable
public class SchedulePropertyName /*implements /alueObject<String>*/{

    @Column(nullable = false)
    /** NotNull */ private String name;

    public SchedulePropertyName(String name) {
        Validate.notNull(name);
        this.name = name;
    }

    public SchedulePropertyName() {
        // HIBERNATE
    }

   /* @Override
    public String getValue() {
        return this.name;
    }*/

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SchedulePropertyName that = (SchedulePropertyName) o;

        return new EqualsBuilder().append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).toHashCode();
    }
}
