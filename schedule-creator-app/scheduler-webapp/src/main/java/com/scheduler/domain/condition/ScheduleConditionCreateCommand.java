package com.scheduler.domain.condition;

// Unused temporarily
public class ScheduleConditionCreateCommand {

    public String ifName;
    public String ifValue;

    public String thenName;
    public String[] thenValues;

    public ScheduleConditionCreateCommand(final String ifName, final String ifValue, final String thenName,
            final String[] thenValues) {
        this.ifName = ifName;
        this.ifValue = ifValue;
        this.thenName = thenName;
        this.thenValues = thenValues;
    }
}
