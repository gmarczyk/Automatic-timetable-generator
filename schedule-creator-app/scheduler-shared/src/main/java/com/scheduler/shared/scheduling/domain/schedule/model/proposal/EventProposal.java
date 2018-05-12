package com.scheduler.shared.scheduling.domain.schedule.model.proposal;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scheduler.shared.scheduling.domain.condition.model.TimeScheduleEntityName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;

@Entity
@Table(name = "eventProposals")
public class EventProposal {

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private EventTimeInterval timeInterval;

    @ElementCollection
    @CollectionTable(name = "proposals2entities")
    private Set<ScheduleProperty> entities = new HashSet<>();

    @Version
    private Long version;

    public EventProposal() {
        // HIBERNATE
    }


    public EventProposal(EventTimeInterval timeInterval, Set<ScheduleProperty> entities) {
        this.timeInterval = timeInterval;
        this.entities = entities == null ? new HashSet<>() : new HashSet<>(entities);
    }

    public EventProposal(EventProposal copy) {
        this.timeInterval = copy.getTimeInterval() == null ? null : new EventTimeInterval(copy.getTimeInterval());
        this.entities = copy.getProperties() == null ? null : new HashSet<>(copy.getProperties());
    }

    public static void fillWithTimeProperties(final EventProposal baseEvent) {
        Set<ScheduleProperty> entities = baseEvent.getProperties();
        EventTimeInterval timeInterval = baseEvent.getTimeInterval();
        if(timeInterval != null) {
            if(timeInterval.hours != null) {
                entities.add(new ScheduleProperty(TimeScheduleEntityName.GODZINA.name(), timeInterval.hours));
            }

            if(timeInterval.day != null) {
                entities.add(new ScheduleProperty(TimeScheduleEntityName.DZIEN.name(), timeInterval.day));
            }
        }

        Set<SchedulePropertyName> names = entities.stream().map(c -> c.propertyName()).collect(Collectors.toSet());
        if (!names.contains(new SchedulePropertyName(TimeScheduleEntityName.DZIEN.name()))) {
            entities.add(new ScheduleProperty(TimeScheduleEntityName.DZIEN.name(), null));
        }
        if (!names.contains(new SchedulePropertyName(TimeScheduleEntityName.GODZINA.name()))) {
            entities.add(new ScheduleProperty(TimeScheduleEntityName.GODZINA.name(), null));
        }
    }

    public static void removeTimeEntities(final EventProposal proposal) {
        Set<ScheduleProperty> entities = proposal.getProperties();
        entities.removeIf(c -> (c.propertyName().getName().equals(TimeScheduleEntityName.DZIEN.name()) || c.propertyName().getName()
                .equals(TimeScheduleEntityName.GODZINA.name())));
    }

    public static void fillIntervalByEntity(EventProposal proposal) {
        if(proposal.getTimeInterval() == null || proposal.getTimeInterval().day == null) {
            Optional<ScheduleProperty> first = proposal.getProperties().stream()
                    .filter(c -> c.propertyName().getName().equals(TimeScheduleEntityName.DZIEN.name()))
                    .findFirst();

            if(first.isPresent()) {
                if(proposal.getTimeInterval() == null) {
                    proposal.setTimeInterval(new EventTimeInterval());
                }
                proposal.getTimeInterval().day = first.get().entityValue().getValue();
            }
        }

        if(proposal.getTimeInterval() == null || proposal.getTimeInterval().hours == null) {
            Optional<ScheduleProperty> first = proposal.getProperties().stream()
                    .filter(c -> c.propertyName().getName().equals(TimeScheduleEntityName.GODZINA.name()))
                    .findFirst();

            if(first.isPresent()) {
                if(proposal.getTimeInterval() == null) {
                    proposal.setTimeInterval(new EventTimeInterval());
                }
                proposal.getTimeInterval().hours = first.get().entityValue().getValue();
            }
        }
    }

    public void setTimeInterval(final EventTimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setEntities(final Set<ScheduleProperty> entities) {
        this.entities = new HashSet<>(entities);
    }

    public EventTimeInterval getTimeInterval() {
        return timeInterval;
    }

    public Set<ScheduleProperty> getProperties() {
        return entities;
    }

    public boolean hasFullTimeSpecified() {
        if( timeInterval == null || timeInterval.day == null || timeInterval.hours == null) {
            return false;
        }
        return true;
    }

    public void describe() {
        System.out.println("--- Event ---  ");
        entities.forEach(entity -> entity.describe());
    }

    //TODO equals hash for name and date

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EventProposal proposal = (EventProposal) o;

        return new EqualsBuilder().append(timeInterval, proposal.timeInterval)
                .append(entities, proposal.entities)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(timeInterval).append(entities).toHashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }


}
