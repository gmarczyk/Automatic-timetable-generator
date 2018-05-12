package com.scheduler.shared.scheduling.application.generator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.GeneratorExtractingFacade;
import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.PropertyGenerationFailure;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@ApplicationService
@Component
// TODO test for when the same event is returned
public class ConditionDrivenEntityProposer {

    private ConflictChecker conflictChecker = new ConflictChecker();
    private GeneratorExtractingFacade generatorExtractingFacade = new GeneratorExtractingFacade();

    public void findAllConditionValidEventVariations(EventProposal baseEvent,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes, Set<EventProposal> results,
            final Set<PropertyGenerationFailure> generationFailures) {

        if (generatorExtractingFacade.hasOnlyUnsetEntities(baseEvent.getProperties())) {
            return;
        }

        this.findAllConditionValidEventVariations(baseEvent, determinantes, results, false, generationFailures);
    }

    private void findAllConditionValidEventVariations(EventProposal baseEvent,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes, Set<EventProposal> results,
            boolean wasModified, final Set<PropertyGenerationFailure> generationFailures) {

        if (!arePropertiesValid(baseEvent, determinantes, generationFailures)) {
            return;
        }

        if (!generatorExtractingFacade.getUnsetDeterminableInEventPropertiesOf(baseEvent, determinantes).isEmpty()) {
            for (final ScheduleProperty unsetProperty : generatorExtractingFacade.getUnsetDeterminableInEventPropertiesOf(
                    baseEvent, determinantes)) {

                Set<SchedulePropertyValue> inCommonElements = generatorExtractingFacade.prepareInCommonPropositionsForPropertyInEvent(
                        baseEvent, determinantes, unsetProperty);

                if (inCommonElements == null) {
                    throw new RuntimeException("Inconsistency not detected properly");
                } else if (inCommonElements.isEmpty()) {
                    continue;
                }

                for (final SchedulePropertyValue inCommonElement : inCommonElements) {
                    EventProposal copy = new EventProposal(baseEvent);
                    copy.getProperties().remove(unsetProperty);
                    copy.getProperties()
                            .add(new ScheduleProperty(unsetProperty.propertyName().getName(), inCommonElement.getValue()));

                    wasModified = true;
                    findAllConditionValidEventVariations(copy, determinantes, results, wasModified, generationFailures);
                }

            }
        } else {
            if (wasModified) {
                results.add(baseEvent);
            }
        }
    }

    private boolean arePropertiesValid(final EventProposal baseEvent,
            final Map<SchedulePropertyName, Set<ScheduleCondition>> determinantes,
            final Set<PropertyGenerationFailure> generationFailures) {

        boolean areValid = true;
        PropertyGenerationFailure propertyGenerationFailure = new PropertyGenerationFailure(baseEvent);

        List<EntityInconsistency> inconsistencies = conflictChecker.findEntityInconsistencyOnEvent(baseEvent,
                determinantes);
        if (!inconsistencies.isEmpty()) {
            propertyGenerationFailure.inconsistencies.addAll(inconsistencies);
            areValid = false;
        }

        List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(baseEvent,
                determinantes);
        if (!collisions.isEmpty()) {
            propertyGenerationFailure.collisions.addAll(collisions);
            areValid = false;
        }

        if (!areValid) {
            generationFailures.add(propertyGenerationFailure);
        }

        return areValid;
    }

}
