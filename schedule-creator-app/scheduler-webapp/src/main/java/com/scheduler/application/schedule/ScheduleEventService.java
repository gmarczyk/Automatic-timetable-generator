package com.scheduler.application.schedule;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.domain.schedule.events.ScheduleEventCreatedEvent;
import com.scheduler.shared.core.structure.ApplicationService;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;

@ApplicationService
@Service
public class ScheduleEventService {

    @Autowired
    private ScheduleEventRepository scheduleEventRepository;

    public void create(ScheduleEventCreatedEvent command) {
        scheduleEventRepository.create(command.teacherVal, command.gradeVal,
                command.subjectVal, command.roomVal, command.hour, command.day);
    }

    public void createAndAssign(ScheduleEventCreatedEvent command) {
        ScheduleEvent scheduleEvent = scheduleEventRepository.create(command.teacherVal, command.gradeVal,
                command.subjectVal, command.roomVal, command.hour, command.day);
        scheduleEvent.setGenerationStatus(ScheduleEvent.GenerationStatus.ASSIGNED);
        scheduleEventRepository.update(scheduleEvent);
    }

    public static ScheduleProperty getWantedEntityOrNullIfNotPresent(ScheduleEvent event, SchoolScheduleEntity schoolScheduleEntity) {
       return Iterables.getOnlyElement(event.getScheduleProperties()
                .stream()
                .filter(c -> c.propertyName().getName().equals(schoolScheduleEntity.toString()))
                .collect(Collectors.toList()),null);
    }

    public void update(final ScheduleEventCreatedEvent command, final long id) {
        ScheduleEvent byId = scheduleEventRepository.findById(id);

        Set<ScheduleProperty> entitySet = new LinkedHashSet<>();
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.SUBJECT.toString(), command.subjectVal.getValue(),true));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.GRADE.toString(), command.gradeVal.getValue()));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.TEACHER.toString(), command.teacherVal.getValue()));
        entitySet.add(new ScheduleProperty(SchoolScheduleEntity.CLASSROOM.toString(), command.roomVal.getValue()));

        byId.setScheduleProperties(entitySet);
        byId.setTimeInterval(new EventTimeInterval(command.day,command.hour));

        scheduleEventRepository.update(byId);
    }
}
