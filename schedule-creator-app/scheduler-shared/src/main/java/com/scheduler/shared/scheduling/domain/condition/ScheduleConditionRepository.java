package com.scheduler.shared.scheduling.domain.condition;

import java.util.List;

import com.scheduler.shared.scheduling.domain.condition.model.ScheduleCondition;

public interface ScheduleConditionRepository {

    List<ScheduleCondition> allConditions();

    void delete(ScheduleCondition onlyElement);

    void create(String value, String value1, String value2, String[] split);
}
