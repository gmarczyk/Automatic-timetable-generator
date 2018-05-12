package com.scheduler.shared.scheduling.application.generator.time;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.application.generator.ConditionDrivenEntityProposer;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.condition.model.TimeScheduleEntityName;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

//Unused class, may be useful after
public class entityTimeProposer {

    private ConditionDrivenEntityProposer conditionDrivenEntityProposer = new ConditionDrivenEntityProposer();

    public void findAllConditionValidTimePropositions(EventProposal baseEvent, Set<EventProposal> resultsContainer,
            final Set<PropertyGenerationFailure> generationFailures,
            Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        final EventProposal copy = prepareWithTimeEntitiesCopy(baseEvent);
        final Map<SchedulePropertyName, Set<ScheduleCondition>> timeDeterminantes = prepareTimeDeterminantes(determinantes);

        conditionDrivenEntityProposer.findAllConditionValidEventVariations(copy, timeDeterminantes,
                resultsContainer, generationFailures);

        int x = 5;
    }

    private EventProposal prepareWithTimeEntitiesCopy(final EventProposal baseEvent) {
        final EventProposal result = new EventProposal(baseEvent);

        Set<ScheduleProperty> entities = result.getProperties();
        Set<SchedulePropertyName> names = entities.stream().map(c -> c.propertyName()).collect(Collectors.toSet());

        if (!names.contains(TimeScheduleEntityName.DZIEN.name())) {
            entities.add(new ScheduleProperty(TimeScheduleEntityName.DZIEN.name(), null));
        }
        if (!names.contains(TimeScheduleEntityName.GODZINA.name())) {
            entities.add(new ScheduleProperty(TimeScheduleEntityName.GODZINA.name(), null));
        }

        return result;
    }

    // TODO to condition extractor or facade and public
    private Map<SchedulePropertyName, Set<ScheduleCondition>> prepareTimeDeterminantes(Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {
        return determinantes.entrySet()
                .stream()
                .filter(c -> ((c.getKey().getName().equals(TimeScheduleEntityName.DZIEN.name()))
                            || c.getKey().getName().equals(TimeScheduleEntityName.GODZINA.name())))
                .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));
    }

}
