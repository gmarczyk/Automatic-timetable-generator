package com.scheduler.shared.scheduling.application.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.GeneratorExtractingFacade;
import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@ApplicationService
@Component
public class ConflictChecker {

    private final GeneratorExtractingFacade generatorExtractingFacade = new GeneratorExtractingFacade();

    public boolean hasAnyConflicts(final EventProposal event,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        List<EntityInconsistency> inconsistencies = findEntityInconsistencyOnEvent(event, determinantes);
        List<ConditionalCollision> collisions = findEntityConditionalCollisionsOnEntities(event, determinantes);

        return (!inconsistencies.isEmpty() || !collisions.isEmpty());
    }

    /**
     * Null entity value is not taken into account when checking for condition
     */
    // TODO MORE TEST FOR THIS?
    public List<EntityInconsistency> findEntityInconsistencyOnEvent(final EventProposal event,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        final List<EntityInconsistency> result = new ArrayList<>();

        for (ScheduleProperty mainEntity : event.getProperties()) {
            for (ScheduleProperty notMainEntity : event.getProperties()) {
                if (!mainEntity.equals(notMainEntity)) {
                    Set<ScheduleCondition> notMainEntityDeterminantes = determinantes.get(notMainEntity.propertyName());
                    if (notMainEntityDeterminantes == null) {
                        continue;
                    }

                    EntityInconsistency entityInconsistency = new EntityInconsistency();
                    entityInconsistency.affectedEntityName = notMainEntity.propertyName();
                    entityInconsistency.actualValue = notMainEntity.entityValue();
                    entityInconsistency.determinantesWantingOtherValue = new HashSet<>();

                    for (final ScheduleCondition condition : notMainEntityDeterminantes) {
                        if (condition.getIf_entity().equals(mainEntity)) {

                            ScheduleProperty actualEntity = findInList(notMainEntity.propertyName(), event.getProperties()); //condition.getThen_entity_values().propertyName(), or just notMainEntity!TODO
                            if (isEntityInconsistentWithCondition(actualEntity, condition)) {
                                entityInconsistency.determinantesWantingOtherValue.add(condition);
                            }
                        }
                    }
                    if (!entityInconsistency.determinantesWantingOtherValue.isEmpty()) {
                        result.add(entityInconsistency);
                    }
                }
            }
        }

        return result;
    }

    public boolean isEntityInconsistentWithCondition(final ScheduleProperty entity, final ScheduleCondition condition) {
        if (condition.getThen_entity_values().isEmpty() || entity == null || entity.entityValue().getValue() == null) {
            return false;// TODO exception? check entityValuefor null before
        }

        Set<String> values = condition.getThen_entity_values()
                .stream()
                .map(c -> c.getValue())
                .collect(Collectors.toSet());

        return !values.contains(entity.entityValue().getValue());
    }

    //TODO do sth with that
    private ScheduleProperty findInList(SchedulePropertyName entityName, Set<ScheduleProperty> entities) {
        for (final ScheduleProperty scheduleProperty : entities) {
            if (entityName.equals(scheduleProperty.propertyName())) {
                return scheduleProperty;
            }
        }
        return null;
    }

    public List<ConditionalCollision> findEntityConditionalCollisionsOnEntities(final EventProposal eventProposal,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes) {

        Set<ScheduleProperty> unsetDeterminableInEventEntitiesOf = generatorExtractingFacade.getUnsetDeterminableInEventPropertiesOf(
                eventProposal, determinantes);

        if(unsetDeterminableInEventEntitiesOf.isEmpty()) {
            return new ArrayList<>();
        }

        List<ConditionalCollision> result = new ArrayList<>();
        for (final ScheduleProperty determinableEntity : unsetDeterminableInEventEntitiesOf) {

            Set<ScheduleCondition> affectingDeterminantes = generatorExtractingFacade.getAffectingDeterminantesFromEntitiesForGivenEntityName(
                    eventProposal.getProperties(), determinableEntity, determinantes);

            Set<Set<SchedulePropertyValue>> allSets = affectingDeterminantes.stream()
                    .map(c -> c.getThen_entity_values())
                    .collect(Collectors.toSet());

            Set<SchedulePropertyValue> allValues = new HashSet<>();
            for (final Set<SchedulePropertyValue> values : allSets) {
                allValues.addAll(values);
            }

            Set<SchedulePropertyValue> inCommonValues = new HashSet<>();
            Set<SchedulePropertyValue> valuesNotPresentInEachSet = new HashSet<>();

            for (final SchedulePropertyValue singleVal : allValues) {
                boolean eachSetContainsVal = true;
                for (final Set<SchedulePropertyValue> singleSet : allSets) {
                    if(!singleSet.contains(singleVal)) {
                        eachSetContainsVal = false;
                        valuesNotPresentInEachSet.add(singleVal);
                    }
                }

                if(eachSetContainsVal) {
                    inCommonValues.add(singleVal);
                }
            }

            ConditionalCollision conditionalCollision = new ConditionalCollision();
            conditionalCollision.conditionsDemandingDifferentValues = new HashSet<>();
            conditionalCollision.affectedEntityName = determinableEntity.propertyName();
            for (final ScheduleCondition determinante : affectingDeterminantes) {
                if(!determinante.getThen_entity_values().stream().anyMatch(inCommonValues::contains)) {
                    conditionalCollision.conditionsDemandingDifferentValues.add(new ScheduleCondition(determinante));
                }
            }

            if(!conditionalCollision.conditionsDemandingDifferentValues.isEmpty())
            result.add(conditionalCollision);

        }

        return result;
    }

}
