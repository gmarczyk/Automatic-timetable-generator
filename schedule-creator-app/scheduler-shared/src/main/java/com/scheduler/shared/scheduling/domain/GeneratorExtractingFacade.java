package com.scheduler.shared.scheduling.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.entity.services.ScheduleEntityExtractor;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@ApplicationService
@Component
public class GeneratorExtractingFacade {

    private ScheduleConditionExtractor conditionExtractor = new ScheduleConditionExtractor();
    private ScheduleEntityExtractor entityExtractor = new ScheduleEntityExtractor();

    /**
     * @return Empty set, when no determinantes for entity with given name found,
     * null if no common elements are present
     */
    /** Nullable */
    public Set<SchedulePropertyValue> prepareInCommonPropositionsForPropertyInEvent(final EventProposal baseEvent,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes, final ScheduleProperty wantedEntity) {

        Set<ScheduleCondition> wantedEntityDeterminantes = determinantes.get(wantedEntity.propertyName());
        if (wantedEntityDeterminantes == null) {
            return new HashSet<>();
        }

        Map<SchedulePropertyName, Set<ScheduleCondition>> propositions = this.conditionExtractor.mapConditionsPerGivenINFLUENCINGEntities(
                wantedEntityDeterminantes, entityExtractor.getSetEntitiesForEvent(baseEvent.getProperties()));

        Set<Set<SchedulePropertyValue>> propositionSets = new HashSet<>();
        for (final Set<ScheduleCondition> set : propositions.values()) {
            Set<SchedulePropertyValue> mergedValues = new HashSet<>();
            set.forEach(condition -> {
                mergedValues.addAll(condition.getThen_entity_values());
            });
            propositionSets.add(mergedValues);
        }

        return entityExtractor.getInCommonElementsFrom(propositionSets);
    }

    //TODO test when line 63 conditions are several, iterator is deleted after all are checked and when only part of them are not influencing, and when all are not influencing etc
    /** NotNull */
    public Set<ScheduleProperty> getUnsetDeterminableInEventPropertiesOf(final EventProposal event,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        Set<ScheduleProperty> unset = new HashSet<>(entityExtractor.getUnsetEntitiesOf(event));
        Set<ScheduleProperty> setEntitiesForEvent = entityExtractor.getSetEntitiesForEvent(event.getProperties());

        Iterator<ScheduleProperty> iterator = unset.iterator();
        while (iterator.hasNext()) {
            ScheduleProperty entity = iterator.next();
            if (!determinantes.keySet().contains(entity.propertyName())) {
                iterator.remove();
            } else { // TODO test this else
                int cantBeDetermined = 0;
                for (final ScheduleCondition condition : determinantes.get(entity.propertyName())) {
                    if(!setEntitiesForEvent.contains(condition.getIf_entity())){
                        cantBeDetermined++;
                    }
                }

                if(cantBeDetermined == determinantes.get(entity.propertyName()).size()) {
                    iterator.remove();
                }
            }
        }

        return unset;
    }


    //TODO TEST
    public boolean hasOnlyUnsetEntities(Set<ScheduleProperty> entities) {
        List<ScheduleProperty> collect = entities.stream()
                .filter(c -> c.entityValue().getValue() == null)
                .collect(Collectors.toList());
        return collect.size() == entities.size();
    }

    public Set<ScheduleCondition> getAffectingDeterminantesFromEntitiesForGivenEntityName(Set<ScheduleProperty> entities,
            ScheduleProperty givenEntity, final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        Set<ScheduleProperty> setEntitiesForEvent = entityExtractor.getSetEntitiesForEvent(entities);

        Set<ScheduleCondition> givenEntityDeterminantes = new HashSet<>(determinantes.get(givenEntity.propertyName()));
        Iterator<ScheduleCondition> iterator = givenEntityDeterminantes.iterator();
        while(iterator.hasNext()) {
            ScheduleCondition next = iterator.next();
            if(!setEntitiesForEvent.contains(next.getIf_entity())) {
                iterator.remove();
            }
        }

        return givenEntityDeterminantes;
    }

}
