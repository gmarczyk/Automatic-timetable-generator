package pl.shared.scheduling.application.generator;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.services.ScheduleConsistencyChecker;

import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.Schedule;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposalBuilder;


public class ScheduleConsistencyCheckerTest {


    @Test
    public void canBeSetInSchedule()  {

        ScheduleProperty[] scheduleEntities = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("SUBJECT", null),
                new ScheduleProperty("GRADE", "3A"),
                new ScheduleProperty("ROOM", null)
        };
        EventTimeInterval timeInterval = new EventTimeInterval("MONDAY","13:30-14:00" );
        EventProposal notFree = new EventProposalBuilder().setEntities(scheduleEntities).setTimeInterval(timeInterval).build();
        // TEACHER JOHN and Grade 3A are not free on 13:30-14:30


        ScheduleProperty[] entities2 = {
                new ScheduleProperty("TEACHER", "JOHN"),
                new ScheduleProperty("SUBJECT", "PHYSICS"),
                new ScheduleProperty("GRADE", "5A"),
                new ScheduleProperty("ROOM", null)
        };
        EventTimeInterval time2 = new EventTimeInterval("MONDAY", "13:30-14:00" );
        EventProposal johnIsTaken = new EventProposalBuilder().setEntities(entities2).setTimeInterval(time2).build();

        Schedule schedule = new Schedule();
        schedule.addEvent(new ScheduleEvent(johnIsTaken));


        ScheduleConsistencyChecker scheduleConsistencyChecker = new ScheduleConsistencyChecker();

        boolean b = scheduleConsistencyChecker.canBeSetInSchedule(johnIsTaken, schedule);

        Assertions.assertFalse(b);

    }

}