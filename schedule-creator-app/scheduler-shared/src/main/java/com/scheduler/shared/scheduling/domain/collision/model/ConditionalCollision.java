package com.scheduler.shared.scheduling.domain.collision.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;

@Entity
@javax.persistence.Table(name = "collisionTable")
public class ConditionalCollision {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    public SchedulePropertyName affectedEntityName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "conditionDemandinId")
    public Set<ScheduleCondition> conditionsDemandingDifferentValues = new HashSet<>();

    @Version
    private Long version;

    public ConditionalCollision() {
        // empty
    }



    public ConditionalCollision(final SchedulePropertyName affectedEntityName,
            final Set<ScheduleCondition> conditionsDemandingDifferentValues) {
        this.affectedEntityName = affectedEntityName;
        this.conditionsDemandingDifferentValues = new HashSet<>(conditionsDemandingDifferentValues);
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ConditionalCollision that = (ConditionalCollision) o;

        return new EqualsBuilder().append(affectedEntityName, that.affectedEntityName)
                .append(conditionsDemandingDifferentValues, that.conditionsDemandingDifferentValues)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(affectedEntityName)
                .append(conditionsDemandingDifferentValues)
                .toHashCode();
    }

    public SchedulePropertyName getAffectedEntityName() {
        return affectedEntityName;
    }

    public void setAffectedEntityName(final SchedulePropertyName affectedEntityName) {
        this.affectedEntityName = affectedEntityName;
    }

    public Set<ScheduleCondition> getConditionsDemandingDifferentValues() {
        return conditionsDemandingDifferentValues;
    }

    public void setConditionsDemandingDifferentValues(final Set<ScheduleCondition> conditionsDemandingDifferentValues) {
        this.conditionsDemandingDifferentValues = new HashSet<>(conditionsDemandingDifferentValues);
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

}
