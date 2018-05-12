package com.scheduler.shared.scheduling.application.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.utils.MathUtils;

import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;
import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.domain.schedule.services.ScheduleConsistencyChecker;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEventConverter;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@ApplicationService
@Component
public class ConditionDrivenScheduleGenerator implements ScheduleGenerator {

    @Autowired
    ScheduleConditionExtractor conditionExtractor;
    @Autowired
    ConditionDrivenEntityProposer conditionDrivenEntityProposer;
    @Autowired
    ConditionAwareTimeProposer conditionAwareTimeProposer;
    @Autowired
    ScheduleConsistencyChecker consistencyChecker;
    @Autowired
    ScheduleEventConverter converter;

    @Override
    public Schedule generate(Set<EventTimeInterval> timeIntervals, List<ScheduleEvent> events, Set<ScheduleCondition> conditions) {
        Schedule schedule = new Schedule();
        timeIntervals.forEach(ti -> schedule.addTimeInterval(ti));
        conditions.forEach(cd -> schedule.addCondition(cd));

        Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes =
                conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(conditions);

        // Generate sets of propositions for determinable events
        for (final ScheduleEvent event : events) {
            EventProposal proposal = converter.convert(event);
            EventProposal.fillWithTimeProperties(proposal);

            Set<EventProposal> results = new HashSet<>();
            Set<PropertyGenerationFailure> failures = new HashSet<>();
            conditionDrivenEntityProposer.findAllConditionValidEventVariations(
                    proposal, determinantes, results, failures );

            if(results.isEmpty() && !failures.isEmpty()) {
                event.setGenerationStatus(ScheduleEvent.GenerationStatus.COLLISION);
                event.setFailures(failures);
                continue;
            }
            else if (results.isEmpty()) {
                event.setGenerationStatus(ScheduleEvent.GenerationStatus.NOT_DETERMINABLE);
            }
            else {
                event.setProposals(results);
            }
        }

        // For those determinable events which have propositions, try putting by capacity
        // If cannot be put, its UNASSIGNABLE
        // Can be mixed for potentially different results
        for (final ScheduleEvent eventWithPropositions : events) {
            if( eventWithPropositions.getProposals() == null || eventWithPropositions.getProposals().isEmpty()) {
                continue;
            }
            
            boolean succesfullyAdded = false;
            Map<Double,Set<EventProposal>> deviations = mapCapacityStdRatioPerProposal(schedule, eventWithPropositions);
            for(int i = 0; i< deviations.keySet().size(); i++ ) {
                Double currentMin = Collections.min(deviations.keySet());

                for (final EventProposal minInProposals : deviations.get(currentMin)) {
                    EventProposal.fillIntervalByEntity(minInProposals);

                    if(!minInProposals.hasFullTimeSpecified()) {
                        Set<EventTimeInterval> validIntervals = conditionAwareTimeProposer.tryFindingValidIntervals(
                                schedule, determinantes, minInProposals);

                        if(validIntervals.isEmpty()) {
                            continue;
                        }
                        else {
                            for (final EventTimeInterval validInterval : validIntervals) {
                                minInProposals.setTimeInterval(validInterval);
                                if(consistencyChecker.canBeSetInSchedule(minInProposals,schedule)) {
                                    EventProposal.removeTimeEntities(minInProposals);
                                    eventWithPropositions.setScheduleProperties(minInProposals.getProperties());
                                    eventWithPropositions.setTimeInterval(minInProposals.getTimeInterval());

                                    schedule.addEvent(eventWithPropositions);
                                    succesfullyAdded = true;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        if(!consistencyChecker.canBeSetInSchedule(minInProposals,schedule)) {
                            continue;
                        }

                        EventProposal.removeTimeEntities(minInProposals);
                        eventWithPropositions.setScheduleProperties(minInProposals.getProperties());
                        eventWithPropositions.setTimeInterval(minInProposals.getTimeInterval());

                        schedule.addEvent(eventWithPropositions);
                        succesfullyAdded = true;
                    }

                    if(succesfullyAdded) {
                        break;
                    }
                }

                deviations.remove(currentMin);
                if(succesfullyAdded) {
                    break;
                }
            }

            if (!succesfullyAdded) {
                eventWithPropositions.setGenerationStatus(ScheduleEvent.GenerationStatus.UNASSIGNABLE);
            }
        }

        return schedule;
    }

    /** NotNull */
    private Map<Double,Set<EventProposal>> mapCapacityStdRatioPerProposal(final Schedule schedule,
            final ScheduleEvent eventWithPropositions) {

        Map<EventProposal, Double> deviations = new HashMap<>();
        for (final EventProposal proposal : eventWithPropositions.getProposals()) {

            List<Double> data = new ArrayList<>();
            for (final ScheduleProperty proposalEntity : proposal.getProperties()) {
                if(proposalEntity.getNonUnique()) {
                    continue;
                }

                Integer capacity= schedule.getPerEntityCapacity().get(proposalEntity);
                if(capacity == null) {
                    capacity = 0;
                }
                data.add(new Double(capacity+1));
            }

            deviations.put(proposal, (MathUtils.sum(data)/data.size()) + MathUtils.getStdDev(data.toArray(new Double[data.size()])));
        }


        Map<Double,Set<EventProposal>> reverted = new HashMap<>();
        for (final Map.Entry<EventProposal, Double> entry : deviations.entrySet()) {
            if (reverted.get(entry.getValue()) != null) {
                reverted.get(entry.getValue()).add(entry.getKey());
            } else {

                Set<EventProposal> proposals = new HashSet<>();
                proposals.add(entry.getKey());
                reverted.put(entry.getValue(), proposals);
            }
        }

        return reverted;
    }


}
