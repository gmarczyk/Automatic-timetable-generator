package com.scheduler.shared.scheduling.domain.schedule.model;

import org.springframework.stereotype.Component;

import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;

@Component
public class ScheduleEventConverter {

    public EventProposal convert(final ScheduleEvent scheduleEvent) {
        /*Set<ScheduleProperty> entities = scheduleEvent.getScheduleProperties().stream()
                .map(c -> c.getScheduleEntity())
                .collect(Collectors.toSet());*/

        return new EventProposalBuilder().setEntities(scheduleEvent.getScheduleProperties())
                .setTimeInterval(scheduleEvent.getTimeInterval())
                .build();
    }
}
