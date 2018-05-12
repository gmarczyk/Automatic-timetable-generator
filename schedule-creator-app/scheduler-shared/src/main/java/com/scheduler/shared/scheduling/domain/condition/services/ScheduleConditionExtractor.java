package com.scheduler.shared.scheduling.domain.condition.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.scheduler.shared.core.structure.DomainService;
import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;

@DomainService
@Component
public class ScheduleConditionExtractor {

    public Map<SchedulePropertyName, Set<ScheduleCondition>> mapConditionsPerEachINFLUENCEDEntityName(
            final Set<ScheduleCondition> conditions) {
        Map<SchedulePropertyName, Set<ScheduleCondition>> affectedBy = new HashMap<>();

        conditions.forEach(condition -> {
            if (!affectedBy.containsKey(condition.getThen_entity_name())) {
                affectedBy.put(condition.getThen_entity_name(), new HashSet<>(Arrays.asList(condition)));
            }
            else {
                affectedBy.get(condition.getThen_entity_name()).add(condition);
            }
        });

        return affectedBy;
    }

    public Map<SchedulePropertyName, Set<ScheduleCondition>> mapConditionsPerGivenINFLUENCINGEntities(
            final Set<ScheduleCondition> conditions, final Set<ScheduleProperty> influencingEntities) {

        Map<SchedulePropertyName, Set<ScheduleCondition>> result = new HashMap<>();

        conditions.forEach(condition -> {
            if(influencingEntities.contains(condition.getIf_entity())) {
                if(!result.containsKey(condition.getIf_entity().propertyName())) {
                    result.put(condition.getIf_entity().propertyName(),
                            new HashSet<ScheduleCondition>(Arrays.asList(condition))); // TODO change for set
                }
                else {
                    result.get(condition.getIf_entity().propertyName()).add(condition);
                }
            }
        });

        return result;
    }

}
