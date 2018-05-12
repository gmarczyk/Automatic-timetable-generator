package com.scheduler.shared.scheduling.domain.condition.model;

import com.scheduler.shared.core.structure.ValueObject;

@ValueObject
public enum TimeScheduleEntityName {

    DZIEN("DZIEN"),
    GODZINA("GODZINA");

    private String days;

    TimeScheduleEntityName(final String days) {
        this.days = days;
    }
}
