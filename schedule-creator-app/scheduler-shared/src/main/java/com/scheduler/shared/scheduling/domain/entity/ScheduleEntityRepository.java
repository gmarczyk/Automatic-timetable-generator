package com.scheduler.shared.scheduling.domain.entity;

import java.util.Set;

import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;

public interface ScheduleEntityRepository {

    Set<ScheduleProperty> list();

}
