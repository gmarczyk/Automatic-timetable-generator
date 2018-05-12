package com.scheduler.shared.scheduling.domain.schedule.model.proposal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;

public class EventProposalBuilder {
    private EventTimeInterval timeInterval;
    private Set<ScheduleProperty> entities;
    private EventProposal copy;

    public EventProposalBuilder setTimeInterval(final EventTimeInterval timeInterval) {
        this.timeInterval = timeInterval;
        return this;
    }

    public EventProposalBuilder setEntities(final Set<ScheduleProperty> entities) {
        this.entities = entities;
        return this;
    }

    public EventProposalBuilder setEntities(final ScheduleProperty[] entities) {
        this.entities = new HashSet<>(Arrays.asList(entities));
        return this;
    }

    public EventProposalBuilder setCopy(final EventProposal copy) {
        this.copy = copy;
        return this;
    }

    public EventProposal build() {
        return new EventProposal(timeInterval, entities);
    }
}