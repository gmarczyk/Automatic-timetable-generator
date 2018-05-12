package com.scheduler.shared.scheduling.domain.schedule;

import java.util.Set;

import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;

public interface ScheduleRepository {

    Set<Schedule> list();

}
