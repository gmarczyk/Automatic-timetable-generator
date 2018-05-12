package com.scheduler.interfaces.web.schedule;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.domain.classrooms.Classroom;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.domain.schedule.events.ScheduleEventCreatedEvent;
import com.scheduler.domain.schedule.events.ScheduleEventCreatedAndAssignedEvent;
import com.scheduler.domain.schedule.events.ScheduleEventUpdatedEvent;
import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.interfaces.web.scheduleevents.ShowCollisionsWindow;
import com.scheduler.interfaces.web.scheduleevents.ShowInconsistenciesView;
import com.scheduler.shared.core.ResultCallback;
import com.scheduler.shared.event.application.EventPublisher;
import com.scheduler.shared.scheduling.application.generator.ConflictChecker;
import com.scheduler.shared.scheduling.domain.condition.services.ScheduleConditionExtractor;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.services.ScheduleConsistencyChecker;
import com.scheduler.shared.scheduling.domain.collision.model.ConditionalCollision;
import com.scheduler.shared.scheduling.domain.collision.model.EntityInconsistency;
import com.scheduler.shared.scheduling.domain.condition.ScheduleConditionRepository;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.model.EventTimeInterval;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;
import com.scheduler.shared.scheduling.domain.schedule.model.proposal.EventProposal;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable(preConstruction = true)
public class ModifyScheduleEventWindow {

    @Autowired
    private ScheduleEventRepository scheduleEventRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ClassroomRepository classRepository;
    @Autowired
    GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ScheduleConsistencyChecker consistencyChecker;
    @Autowired
    private ConflictChecker conflictChecker;
    @Autowired
    private ScheduleConditionExtractor conditionExtractor;
    @Autowired
    private ScheduleConditionRepository conditionRepository;

    private final MainUIView mv;
    private VerticalLayout layout;
    private Window window;

    private ComboBox<SchedulePropertyValue> subjectCombo = new ComboBox<>(SchoolScheduleEntity.SUBJECT.toString());
    private ComboBox<SchedulePropertyValue> gradeCombo = new ComboBox<>(SchoolScheduleEntity.GRADE.toString());
    private ComboBox<SchedulePropertyValue> teacherCombo = new ComboBox<>(SchoolScheduleEntity.TEACHER.toString());
    private ComboBox<SchedulePropertyValue> roomCombo = new ComboBox<>(SchoolScheduleEntity.CLASSROOM.toString());

    private Button addButton = new Button("Dodaj");
    private Button testAbilityToSettle = new Button("Sprawdz mozliwosc");

    ResultCallback callback;

    public ModifyScheduleEventWindow(MainUIView mv, ScheduleEvent ref, String day, String hour, ResultCallback<Boolean> callback) {
        this.mv = mv;
        this.callback = callback;
        this.window = new Window("Dodaj zdarzenie");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        layout = new VerticalLayout();
        layout.setMargin(true);

        HorizontalLayout flay = new HorizontalLayout();
        flay.setSizeFull();

        flay.addComponents(subjectCombo, gradeCombo, teacherCombo, roomCombo );
        flay.setComponentAlignment(subjectCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(gradeCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(teacherCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(roomCombo, Alignment.TOP_CENTER);

        layout.addComponents(flay, testAbilityToSettle, addButton);
        layout.setComponentAlignment(addButton, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(testAbilityToSettle, Alignment.BOTTOM_CENTER);

        window.setContent(layout);
        window.center();

        Set<ScheduleProperty> entities =  new HashSet<>();
        for (final Teacher x : teacherRepository.list()) {
            entities.add(x.getScheduleEntity());
        }
        for (final Classroom x : classRepository.allClassrooms()) {
            entities.add(x.getScheduleEntity());
        }
        for (final Grade x : gradeRepository.allGrades()) {
            entities.add(x.getScheduleEntity());
        }
        for (final Subject x : subjectRepository.allSubjects()) {
            entities.add(x.getScheduleEntity());
        }

        teacherCombo.setItems(valuesForEntity(new SchedulePropertyName(SchoolScheduleEntity.TEACHER.toString()),entities));
        gradeCombo.setItems(valuesForEntity(new SchedulePropertyName(SchoolScheduleEntity.GRADE.toString()),entities));
        roomCombo.setItems(valuesForEntity(new SchedulePropertyName(SchoolScheduleEntity.CLASSROOM.toString()),entities));
        subjectCombo.setItems(valuesForEntity(new SchedulePropertyName(SchoolScheduleEntity.SUBJECT.toString()),entities));

        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                ScheduleEventCreatedEvent c = new ScheduleEventCreatedEvent(
                        teacherCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        gradeCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        subjectCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        roomCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        hour, day);

                if(ref != null && ref.getGenerationStatus().equals(ScheduleEvent.GenerationStatus.ASSIGNED)) {
                    EventPublisher.publish(new ScheduleEventUpdatedEvent(c, ref.getId()));
                }
                else {
                    EventPublisher.publish(new ScheduleEventCreatedAndAssignedEvent(c));
                }

                callback.callback(true);
                window.close();
            }
        });

        testAbilityToSettle.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                // Used temporarily, without sending actually
                ScheduleEventCreatedEvent c = new ScheduleEventCreatedEvent(
                        teacherCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        gradeCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        subjectCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        roomCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                        hour, day);

                Set<ScheduleProperty> entitySet = new LinkedHashSet<>();
                entitySet.add(new ScheduleProperty(SchoolScheduleEntity.SUBJECT.toString(), c.subjectVal.getValue(),true));
                entitySet.add(new ScheduleProperty(SchoolScheduleEntity.GRADE.toString(), c.gradeVal.getValue()));
                entitySet.add(new ScheduleProperty(SchoolScheduleEntity.TEACHER.toString(), c.teacherVal.getValue()));
                entitySet.add(new ScheduleProperty(SchoolScheduleEntity.CLASSROOM.toString(), c.roomVal.getValue()));

                ScheduleEvent scheduleEvent = new ScheduleEvent(new EventTimeInterval(c.day, c.hour), entitySet);


                List<ScheduleEvent> allAssigned = scheduleEventRepository.allEvents()
                        .stream()
                        .filter(x -> x.getGenerationStatus().equals(ScheduleEvent.GenerationStatus.ASSIGNED))
                        .collect(Collectors.toList());
                allAssigned.remove(ref);

                EventProposal eventProposal = new EventProposal(scheduleEvent.getTimeInterval(),
                        scheduleEvent.getScheduleProperties());

                EventProposal.fillWithTimeProperties(eventProposal);
                List<ConditionalCollision> collisions = conflictChecker.findEntityConditionalCollisionsOnEntities(
                        eventProposal, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(
                                new HashSet<>(conditionRepository.allConditions())));

                List<EntityInconsistency> entityInconsistencyOnEvent = conflictChecker.findEntityInconsistencyOnEvent(
                        eventProposal, conditionExtractor.mapConditionsPerEachINFLUENCEDEntityName(
                                new HashSet<>(conditionRepository.allConditions())));

                boolean valid = true;
                if(collisions.size() > 0) {
                    valid = false;
                    new ShowCollisionsWindow(mv,collisions).show();
                }

                if(entityInconsistencyOnEvent.size()>0) {
                    valid = false;
                    new ShowInconsistenciesView(mv,entityInconsistencyOnEvent).show();
                }

                if(!consistencyChecker.canBeSetInSchedule(eventProposal,allAssigned)) {
                    valid = false;
                    Notification.show("Konflikt miejsc! Zdarzenie nie powinno byc umieszczone w planie");
                }

                if(valid) {
                    Notification.show("Zdarzenie moze zostac zagniezdzone w harmonogramie");
                }

            }
        });
    }


    private Set<SchedulePropertyValue> valuesForEntity(final SchedulePropertyName name, final Set<ScheduleProperty> entities) {
        Set<SchedulePropertyValue>  valuesToBeSet = new HashSet<>();
        if(name == null || name.getName() == null) {
            return valuesToBeSet;
        }

        for (final ScheduleProperty e : entities) {
            if(e.propertyName().equals(name)) {
                valuesToBeSet.add(e.entityValue());
            }
        }
        return valuesToBeSet;
    }

    public void show() {
        mv.addWindow(window);
    }
}
