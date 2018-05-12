package com.scheduler.shared.scheduling.domain.entity.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.DomainService;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;

@DomainService
@Component
public class ScheduleEntityExtractor {

    public Set<ScheduleProperty> getUnsetEntitiesOf(final EventProposal event) {
        return filterSetByPredicate(c -> c.entityValue().getValue() == null, event.getProperties());
    }

    public Set<ScheduleProperty> getSetEntitiesForEvent(final Set<ScheduleProperty> entities) {
        return filterSetByPredicate(c -> c.entityValue().getValue() != null, entities);
    }

    private Set<ScheduleProperty> filterSetByPredicate(final Predicate<ScheduleProperty> predicate,
            final Set<ScheduleProperty> entities) {

        return entities.stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * @return null if any of sets contains a value that is not present in the other sets, all in common values if
     * such are present, empty set if nothing to search in
     */
    /** Nullable */
    public Set<SchedulePropertyValue> getInCommonElementsFrom(final Set<Set<SchedulePropertyValue>> sets) {
        Set<SchedulePropertyValue> result = new HashSet<>();

        if(sets.size() > 1 ){
            for (final Set<SchedulePropertyValue> set : sets) {
                Collection<Set<SchedulePropertyValue>> values = sets;
                values.remove(set);
                for (final Set<SchedulePropertyValue> singleOtherSet : values) {
                    set.retainAll(singleOtherSet);
                    if(set.isEmpty()) {
                        return null;
                    }
                }

                result = set;
                break; //outerLoop once - first with all others
            }
        }
        else if(sets.size() == 1){
            for (final Set<SchedulePropertyValue> singleProp : sets) {
                return singleProp; // only one set ocontains values
            }
        }
        else {
            return result;
        }

        return result;
    }

}
