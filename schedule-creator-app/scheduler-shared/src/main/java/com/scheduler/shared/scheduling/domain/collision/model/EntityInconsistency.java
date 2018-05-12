package com.scheduler.shared.scheduling.domain.collision.model;

import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;

@Entity
@Table(name = "inconsistencies")
public class EntityInconsistency {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    public SchedulePropertyName affectedEntityName;
    @Embedded
    public SchedulePropertyValue actualValue;

    @OneToMany
    @JoinColumn(name = "determinantesWantingIdInIncons")
    public Set<ScheduleCondition> determinantesWantingOtherValue;

    @Version
    private Long version;

    public EntityInconsistency() {
        // HIBERNATE
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EntityInconsistency that = (EntityInconsistency) o;

        return new EqualsBuilder().append(affectedEntityName, that.affectedEntityName)
                .append(actualValue, that.actualValue)
                .append(determinantesWantingOtherValue, that.determinantesWantingOtherValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(affectedEntityName)
                .append(actualValue)
                .append(determinantesWantingOtherValue)
                .toHashCode();
    }

    public SchedulePropertyName getAffectedEntityName() {
        return affectedEntityName;
    }

    public void setAffectedEntityName(final SchedulePropertyName affectedEntityName) {
        this.affectedEntityName = affectedEntityName;
    }

    public SchedulePropertyValue getActualValue() {
        return actualValue;
    }

    public void setActualValue(final SchedulePropertyValue actualValue) {
        this.actualValue = actualValue;
    }

    public Set<ScheduleCondition> getDeterminantesWantingOtherValue() {
        return determinantesWantingOtherValue;
    }

    public void setDeterminantesWantingOtherValue(final Set<ScheduleCondition> determinantesWantingOtherValue) {
        this.determinantesWantingOtherValue = determinantesWantingOtherValue;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }


}
