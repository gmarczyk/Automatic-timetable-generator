package com.scheduler.interfaces.web.scheduleevents;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.domain.classrooms.Classroom;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.domain.schedule.events.ScheduleEventCreatedEvent;
import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.shared.event.application.EventPublisher;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable(preConstruction = true)
public class AddScheduleEventWindow {

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

    public static final String[] PRE_DAYS = {"PONIEDZIALEK","WTOREK","SRODA","CZWARTEK","PIATEK"};
    public static final String[] PRE_HOURS = {"8:00-8:45" , "9:00-9:45" , "10:00-10:45", "11:00-11:45", "12:00-12:45",
            "13:00-13:45", "14:00-14:45", "15:00-15:45", "16:00-16:45"};

    private final MainUIView mv;
    private VerticalLayout layout;
    private Window window;

    private ComboBox<SchedulePropertyValue> subjectCombo = new ComboBox<>(SchoolScheduleEntity.SUBJECT.toString());
    private ComboBox<SchedulePropertyValue> gradeCombo = new ComboBox<>(SchoolScheduleEntity.GRADE.toString());
    private ComboBox<SchedulePropertyValue> teacherCombo = new ComboBox<>(SchoolScheduleEntity.TEACHER.toString());
    private ComboBox<SchedulePropertyValue> roomCombo = new ComboBox<>(SchoolScheduleEntity.CLASSROOM.toString());
    private ComboBox<String> dayCombo = new ComboBox<>("Dzien");
    private ComboBox<String> hourCombo = new ComboBox<>("Godzina");
    private Button addButton = new Button("Dodaj");
    private TextField multiple = new TextField("Stworz kilka");

    public AddScheduleEventWindow(MainUIView mv) {
        this.mv = mv;
        this.window = new Window("Dodaj zdarzenie");
        this.window.setWidth(60, Sizeable.Unit.PERCENTAGE);

        layout = new VerticalLayout();
        layout.setMargin(true);

        HorizontalLayout flay = new HorizontalLayout();
        flay.setSizeFull();

        flay.addComponents(subjectCombo, gradeCombo, teacherCombo, roomCombo, dayCombo, hourCombo);
        flay.setComponentAlignment(subjectCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(gradeCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(teacherCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(roomCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(dayCombo, Alignment.TOP_CENTER);
        flay.setComponentAlignment(hourCombo, Alignment.TOP_CENTER);

        layout.addComponents(flay, addButton, multiple);
        layout.setComponentAlignment(addButton, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(multiple, Alignment.BOTTOM_CENTER);

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
        hourCombo.setItems(PRE_HOURS);
        dayCombo.setItems(PRE_DAYS);

        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                int i = 1;
                if(multiple.getValue() != null && StringUtils.isNotBlank(multiple.getValue())) {
                    i = Integer.parseInt(multiple.getValue());
                }

                for(int j = 0; j < i; j++) {
                    ScheduleEventCreatedEvent eventCreated = new ScheduleEventCreatedEvent(
                            teacherCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                            gradeCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                            subjectCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                            roomCombo.getSelectedItem().orElse(new SchedulePropertyValue(null)),
                            hourCombo.getSelectedItem().orElse(null), dayCombo.getSelectedItem().orElse(null));
                    EventPublisher.publish(eventCreated);
                }
                window.close();
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
