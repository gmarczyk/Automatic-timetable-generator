package pl.shared.scheduling;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;

public class SchedulingBuildingHelper {

    public Set<SchedulePropertyValue> valuesSet(String[] values) {
        Set<SchedulePropertyValue> result = new HashSet<>();
        for (final String value : values) {
            result.add(new SchedulePropertyValue(value));
        }
        return result;
    }

    public Set<SchedulePropertyName> namesSet(String[] values) {
        Set<SchedulePropertyName> result = new HashSet<>();
        for (final String value : values) {
            result.add(new SchedulePropertyName(value));
        }
        return result;
    }

    public Set<ScheduleProperty> entitiesSet(Map<String, String> values) {
        Set<ScheduleProperty> result = new HashSet<>();
        values.forEach((n, v) -> {
            result.add(new ScheduleProperty(n, v));
        });
        return result;
    }

}
