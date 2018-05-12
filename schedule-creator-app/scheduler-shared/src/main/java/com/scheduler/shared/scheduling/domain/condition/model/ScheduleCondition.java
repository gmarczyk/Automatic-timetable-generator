package com.scheduler.shared.scheduling.domain.condition.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.users.domain.multitenancy.TenantAware;
import com.scheduler.shared.users.domain.multitenancy.TenantId;

@Entity
@Table(name = "schedule_conditions")
public class ScheduleCondition implements TenantAware {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private TenantId tenantId;

    @Embedded
    protected ScheduleProperty if_entity;

    @Embedded
    protected SchedulePropertyName then_entity_name;

    @ElementCollection
    @CollectionTable(name = "schedule_conditions2then_entity_values")
    protected Set<SchedulePropertyValue> then_entity_values = new HashSet<>();

    @Version
    private Long version;


    public ScheduleCondition(final String ifName, final String ifValue, final String thenName,
            final String[] thenValues) {
        this.if_entity = new ScheduleProperty(ifName,ifValue);
        this.then_entity_name = new SchedulePropertyName(thenName);
        Set<SchedulePropertyValue> vals = new HashSet<>();
        for (final String tv : thenValues) {
            vals.add(new SchedulePropertyValue(tv));
        }
        this.then_entity_values = vals;
    }

    public ScheduleCondition(ScheduleCondition copy) {
        this.if_entity = new ScheduleProperty(copy.if_entity.getSchedulePropertyName().getName(),
                copy.if_entity.getSchedulePropertyValue().getValue(),copy.if_entity.getNonUnique());

        this.then_entity_name = new SchedulePropertyName(copy.getThen_entity_name().getName());

        this.then_entity_values = new HashSet<>(copy.getThen_entity_values());
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(final TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public void setIf_entity(final ScheduleProperty if_entity) {
        this.if_entity = if_entity;
    }

    public void setThen_entity_name(final SchedulePropertyName then_entity_name) {
        this.then_entity_name = then_entity_name;
    }

    public void setThen_entity_values(final Set<SchedulePropertyValue> then_entity_values) {
        this.then_entity_values = then_entity_values;
    }

    public ScheduleCondition(ScheduleProperty if_entity,SchedulePropertyName then_entity_name, Set<SchedulePropertyValue> then_entity) {
        this.if_entity = if_entity;
        this.then_entity_name = then_entity_name;
        this.then_entity_values = then_entity;
    }

    public ScheduleCondition(ScheduleProperty if_entity,  SchedulePropertyName then_entity_name, String[] then_entity) {
        this.if_entity = if_entity;
        this.then_entity_name = then_entity_name;
        Set<SchedulePropertyValue> values = new HashSet<>();
        for (final String s : then_entity) {
            values.add(new SchedulePropertyValue(s));
        }
        this.then_entity_values = values;
    }

    public ScheduleCondition() {
         // HIBERNATE
    }

    public ScheduleProperty getIf_entity() {
        return if_entity;
    }

    public Set<SchedulePropertyValue> getThen_entity_values() {
        return then_entity_values;
    }

    public SchedulePropertyName getThen_entity_name() {
        return then_entity_name;
    }

    @Override
    public String toString() {
        String setString  = "";
        for (final SchedulePropertyValue then : then_entity_values) {
            setString += then_entity_name.getName() + " " + then.getValue() + "\n";
        }

        return if_entity.propertyName().getName() + " " + if_entity.entityValue().getValue() + "\n" + setString;

    }

    // TODO test for equals method !!!

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ScheduleCondition that = (ScheduleCondition) o;

        return new EqualsBuilder().append(id, that.id)
                .append(tenantId, that.tenantId)
                .append(if_entity, that.if_entity)
                .append(then_entity_name, that.then_entity_name)
                .append(then_entity_values, that.then_entity_values)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id)
                .append(tenantId)
                .append(if_entity)
                .append(then_entity_name)
                .append(then_entity_values)
                .toHashCode();
    }

    @Override
    public TenantId ownerTenantId() {
        return tenantId;
    }

    @Override
    public void setOwnerTenantId(final TenantId tenantId) {
        this.tenantId=tenantId;
    }
}
