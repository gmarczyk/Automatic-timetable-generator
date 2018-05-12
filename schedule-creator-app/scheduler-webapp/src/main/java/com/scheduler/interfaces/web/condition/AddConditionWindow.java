package com.scheduler.interfaces.web.condition;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.classrooms.Classroom;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.interfaces.web.scheduleevents.AddScheduleEventWindow;
import com.scheduler.shared.scheduling.domain.condition.ScheduleConditionRepository;
import com.scheduler.shared.scheduling.domain.condition.model.TimeScheduleEntityName;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyName;
import com.scheduler.shared.scheduling.domain.entity.model.SchedulePropertyValue;
import com.vaadin.data.HasValue;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable(preConstruction = true)
public class AddConditionWindow {

    private final MainUIView mv;
    private VerticalLayout layout;
    private Window window;

    private ComboBox<SchedulePropertyName> ifName = new ComboBox<>("Jezeli wlasciwosc");
    private ComboBox<SchedulePropertyValue> ifValue = new ComboBox<>("Przyjmie wartosc");

    private ComboBox<SchedulePropertyName> thenName = new ComboBox<>("To wymus na wlasciwosci");
    private TextField thenVals = new TextField("Ktoras z wartosci");

    private Button addButton = new Button("Dodaj");

    @Autowired
    private ScheduleConditionRepository scheduleConditionRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ClassroomRepository classRepository;
    @Autowired
    GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public AddConditionWindow(MainUIView mv) {
        this.mv = mv;
        this.window = new Window("Dodaj warunek");
        this.window.setWidth(50, Sizeable.Unit.PERCENTAGE);

        layout = new VerticalLayout();
        layout.setMargin(true);

        HorizontalLayout flay = new HorizontalLayout();
        HorizontalLayout secLay = new HorizontalLayout();
        flay.setSizeFull();
        secLay.setSizeFull();

        flay.addComponents(ifName,ifValue,thenName);
        flay.setComponentAlignment(ifName, Alignment.TOP_CENTER);
        flay.setComponentAlignment(ifValue, Alignment.TOP_CENTER);
        flay.setComponentAlignment(thenName, Alignment.TOP_CENTER);

        secLay.addComponents(thenVals);
        secLay.setComponentAlignment(thenVals, Alignment.TOP_CENTER);
        layout.addComponents(flay,secLay, addButton);
        layout.setComponentAlignment(addButton, Alignment.BOTTOM_CENTER);

        window.setContent(layout);
        window.center();

        Set<ScheduleProperty> entities =  new LinkedHashSet<>();
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

        Set<SchedulePropertyName> names = entities.stream().map(c -> c.propertyName()).collect(Collectors.toSet());
        LinkedHashSet<SchedulePropertyName> a = new LinkedHashSet<SchedulePropertyName>() {{
            add(new SchedulePropertyName(TimeScheduleEntityName.DZIEN.name()));
            add(new SchedulePropertyName(TimeScheduleEntityName.GODZINA.name()));
        }};

        names.addAll(a);

        ifName.setItems(names);
        thenName.setItems(names);
        SchedulePropertyName schedulePropertyName = ifName.getSelectedItem().orElse(null);
        ifValue.setItems(valuesForEntity(schedulePropertyName,entities));


        ifName.addValueChangeListener(new HasValue.ValueChangeListener<SchedulePropertyName>() {
            @Override
            public void valueChange(final HasValue.ValueChangeEvent<SchedulePropertyName> valueChangeEvent) {
                SchedulePropertyName value = valueChangeEvent.getValue();
                ifValue.setItems(valuesForEntity(value, entities));
            }
        });

        addButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                scheduleConditionRepository.create(ifName.getSelectedItem().get().getName(),
                        ifValue.getSelectedItem().get().getValue(),
                        thenName.getSelectedItem().get().getName(),
                        thenVals.getValue().split(","));

                window.close();
            }
        });
    }

    private Set<SchedulePropertyValue>  valuesForEntity(final SchedulePropertyName name, final Set<ScheduleProperty> entities) {
        Set<SchedulePropertyValue>  valuesToBeSet = new LinkedHashSet<>();
        if(name == null || name.getName() == null) {
            return valuesToBeSet;
        }

        if(name.getName().equals(TimeScheduleEntityName.DZIEN.name())) {
            for (final String preDay : AddScheduleEventWindow.PRE_DAYS) {
                valuesToBeSet.add(new SchedulePropertyValue(preDay));
            }
            return valuesToBeSet;
        }

        if(name.getName().equals(TimeScheduleEntityName.GODZINA.name())) {
            for (final String preDay : AddScheduleEventWindow.PRE_HOURS) {
                valuesToBeSet.add(new SchedulePropertyValue(preDay));
            }
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
