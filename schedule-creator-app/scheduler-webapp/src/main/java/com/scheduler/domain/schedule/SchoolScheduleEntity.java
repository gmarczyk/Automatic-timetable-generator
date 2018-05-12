package com.scheduler.domain.schedule;


public enum SchoolScheduleEntity {

    TEACHER("Nauczyciel"),
    CLASSROOM("Pokoj"),
    GRADE("Grupa"),
    SUBJECT("Przedmiot");

    private String val;

    SchoolScheduleEntity(final String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
