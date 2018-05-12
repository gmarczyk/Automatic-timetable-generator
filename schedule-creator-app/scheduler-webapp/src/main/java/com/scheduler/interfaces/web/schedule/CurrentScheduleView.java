package com.scheduler.interfaces.web.schedule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.domain.schedule.SchoolScheduleEntity;
import com.scheduler.domain.classrooms.Classroom;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.SubjectRepository;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.domain.teacher.TeacherRepository;
import com.scheduler.interfaces.web.scheduleevents.AddScheduleEventWindow;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.core.ResultCallback;
import com.scheduler.shared.scheduling.domain.entity.model.ScheduleProperty;
import com.scheduler.shared.scheduling.domain.schedule.model.ScheduleEvent;
import com.scheduler.shared.scheduling.domain.schedule.ScheduleEventRepository;
import com.vaadin.data.HasValue;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Configurable(preConstruction = true)
public class CurrentScheduleView implements RefreshableLayout {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private ClassroomRepository classRepository;
    @Autowired
    GradeRepository gradeRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ScheduleEventRepository scheduleEventRepository;

    private MainUIView mv;
    private ComboBox<ScheduleProperty> scheduleEntityComboBox = new ComboBox<>("Plan dla");
    private final Button refreshButton = new Button("Odswiez");

    public CurrentScheduleView(final MainUIView mainUIView) {
        this.mv = mainUIView;

        initData(null);
        prepareScheduleViewContent(null);
    }

    private void initData(ScheduleProperty property) {
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
        entities = entities.stream().filter(c->c.getNonUnique() == false).collect(Collectors.toSet());
        scheduleEntityComboBox.setItems(entities);
        scheduleEntityComboBox.addValueChangeListener(new HasValue.ValueChangeListener<ScheduleProperty>() {
            @Override
            public void valueChange(final HasValue.ValueChangeEvent<ScheduleProperty> valueChangeEvent) {
                ScheduleProperty value = valueChangeEvent.getValue();
                prepareScheduleViewContent(value);
            }
        });
        if(property != null ) {
            scheduleEntityComboBox.setValue(property);
        }

        refreshButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                refreshLay();
            }
        });
    }

    private void prepareScheduleViewContent(ScheduleProperty scheduleForEntity) {
        HorizontalLayout mainLay = new HorizontalLayout();
        mainLay.setSizeFull();
        mainLay.setMargin(true);

        VerticalLayout verLay = new VerticalLayout();
        //verLay.setSizeFull();
        verLay.addComponents(scheduleEntityComboBox);

        Component component = prepareScheduleForEntity(scheduleForEntity);

        mainLay.addComponent(component);
        mainLay.addComponent(verLay);
        mainLay.setExpandRatio(component,7.0f);
        mainLay.setExpandRatio(verLay,3.0f);

        mv.setMainContent(mainLay);
    }

    private Component prepareScheduleForEntity(ScheduleProperty entity) {
        if(entity==null) {
            entity = new ScheduleProperty("just","letItGo");
        }

        List<ScheduleEvent> scheduleEvents = scheduleEventRepository.allEvents();
        Set<ScheduleEvent> assignedEvents = scheduleEvents.stream()
                .filter(c -> c.getGenerationStatus().equals(ScheduleEvent.GenerationStatus.ASSIGNED))
                .collect(Collectors.toSet());
        Set<ScheduleEvent> forEntityEvents = prepareEventsForGivenEntity(assignedEvents,
                entity);


        Set<HourForDaysRowModel> hourmodelsset = new LinkedHashSet<>();
        for (final String eachhour : AddScheduleEventWindow.PRE_HOURS) {
            hourmodelsset.add(new HourForDaysRowModel(eachhour,prepareEventsForGivenHour(forEntityEvents, eachhour)));
        }

        Grid<HourForDaysRowModel> grid = new Grid<>();
        grid.setSizeFull();
        grid.setRowHeight(100);
        grid.setItems(hourmodelsset);
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addComponentColumn(HourForDaysRowModel::getForTime).setCaption("Godzina");
        grid.addComponentColumn(HourForDaysRowModel::getForMonday).setCaption("PONIEDZIALEK");
        grid.addComponentColumn(HourForDaysRowModel::getForTuesday).setCaption("WTOREK");
        grid.addComponentColumn(HourForDaysRowModel::getForWednesday).setCaption("SRODA");
        grid.addComponentColumn(HourForDaysRowModel::getForThursday).setCaption("CZWARTEK");
        grid.addComponentColumn(HourForDaysRowModel::getForFriday).setCaption("PIATEK");

        VerticalLayout sadsa = new VerticalLayout();
        sadsa.setSizeFull();
        sadsa.addComponents(grid);

        return sadsa;
    }

    private Set<ScheduleEvent> prepareEventsForGivenEntity(Set<ScheduleEvent> events, ScheduleProperty entity) {
        Set<ScheduleEvent> res = new HashSet<>();
        for (final ScheduleEvent event : events) {
            if(event.getScheduleProperties().contains(entity)) {
                res.add(event);
            }
        }
        return res;
    }

    private Set<ScheduleEvent> prepareEventsForGivenHour(Set<ScheduleEvent> events, String hour) {
        Set<ScheduleEvent> res = new HashSet<>();
        for (final ScheduleEvent event : events) {
            if(event.getTimeInterval().hours.equals(hour)) {
                res.add(event);
            }
        }
        return res;
    }



    private MyLayout prepareCellLayout(String subj, String room, String grade, String teacher, ScheduleEvent ref, String day, String hour) {
        MyLayout singleCell = new MyLayout(ref,day, hour);

        singleCell.setSpacing(false);
        singleCell.setMargin(false);

        Label labelka = new Label(fixedLengthString(subj));
        labelka.addStyleName("ghj");
        labelka.setSizeFull();
        Label labelka2 = new Label(fixedLengthString(room));
        labelka2.setSizeFull();
        labelka2.addStyleName("ghj");
        Label labelka3 = new Label(fixedLengthString(grade));
        labelka3.setSizeFull();
        labelka3.addStyleName("ghj");
        Label labelka4 = new Label(fixedLengthString(teacher));
        labelka4.setSizeFull();
        labelka4.addStyleName("ghj");

        singleCell.addComponents(labelka,labelka2,labelka3, labelka4);

        singleCell.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent layoutClickEvent) {
               if(scheduleEntityComboBox.getValue() == null) {
                    return;
               }

               /* String status = singleCell.ref.getGenerationStatus().toString();
                String entities = "";
                for (final ScheduleProperty enn : singleCell.ref.getScheduleProperties()) {
                    entities += enn.toString() + " | ";
                }
                Notification.show(status + " " + entities);*/
                new ModifyScheduleEventWindow(mv, ref, singleCell.day, singleCell.hour, new ResultCallback<Boolean>() {
                    @Override
                    public void callback(final Boolean aBoolean) {
                        CurrentScheduleView.this.refreshLay();
                    }
                }).show();
            }
        });

        return singleCell;
    }


    public static String fixedLengthString(String string ) {
        if(string == null) {
            return "?";
        }

        if(string.length() < 3) {
            return string;
        }

        int length =18;
        if(string.length()>length)
            return string.substring(0,length -3) + "...";
        return String.format("%1$"+length+ "s", string);
    }

    @Override
    public void refreshLay() {
        ScheduleProperty value = scheduleEntityComboBox.getValue();
        initData(value);
        prepareScheduleViewContent(value);
    }

    private class HourForDaysRowModel {

        String MONDAY = "PONIEDZIALEK";
        String TUESDAY = "WTOREK";
        String WEDNESDAY = "SRODA";
        String THURSDAY = "CZWARTEK";
        String FRIDAY = "PIATEK";
        Map<String, ScheduleEvent> eventMap = new HashMap<>();

        String hour;
        Set<ScheduleEvent> columns;

        public HourForDaysRowModel(final String hour, final Set<ScheduleEvent> columns) {
            this.hour = hour;
            this.columns = columns;

            eventMap.put(MONDAY, Iterables.getOnlyElement(
                    columns.stream().filter(c -> c.getTimeInterval().day.equals(MONDAY)).collect(Collectors.toList()), null));

            eventMap.put(TUESDAY, Iterables.getOnlyElement(
                    columns.stream().filter(c -> c.getTimeInterval().day.equals(TUESDAY)).collect(Collectors.toList()), null));

            eventMap.put(WEDNESDAY, Iterables.getOnlyElement(
                    columns.stream().filter(c -> c.getTimeInterval().day.equals(WEDNESDAY)).collect(Collectors.toList()), null));

            eventMap.put(THURSDAY, Iterables.getOnlyElement(
                    columns.stream().filter(c -> c.getTimeInterval().day.equals(THURSDAY)).collect(Collectors.toList()), null));

            eventMap.put(FRIDAY, Iterables.getOnlyElement(
                    columns.stream().filter(c -> c.getTimeInterval().day.equals(FRIDAY)).collect(Collectors.toList()), null));

        }

        public MyLayout getForKey(String day) {
            ScheduleEvent scheduleEvent = eventMap.get(day);
            if(scheduleEvent == null) {
                return prepareCellLayout(fixedLengthString(""),fixedLengthString(""),fixedLengthString(""), fixedLengthString(""),null, day, hour);
            }

            ScheduleProperty subjectElement = Iterables.getOnlyElement(scheduleEvent.getScheduleProperties()
                    .stream()
                    .filter(c -> c.propertyName().getName().equals(SchoolScheduleEntity.SUBJECT.toString()))
                    .collect(Collectors.toList()),null);

            ScheduleProperty classElement = Iterables.getOnlyElement(scheduleEvent.getScheduleProperties()
                    .stream()
                    .filter(c -> c.propertyName().getName().equals(SchoolScheduleEntity.CLASSROOM.toString()))
                    .collect(Collectors.toList()),null);

            ScheduleProperty teacherElement = Iterables.getOnlyElement(scheduleEvent.getScheduleProperties()
                    .stream()
                    .filter(c -> c.propertyName().getName().equals(SchoolScheduleEntity.TEACHER.toString()))
                    .collect(Collectors.toList()),null);

            ScheduleProperty gradeelement = Iterables.getOnlyElement(scheduleEvent.getScheduleProperties()
                    .stream()
                    .filter(c -> c.propertyName().getName().equals(SchoolScheduleEntity.GRADE.toString()))
                    .collect(Collectors.toList()),null);

            return prepareCellLayout(subjectElement != null ? fixedLengthString(subjectElement.entityValue().getValue()) : "?",
                    classElement != null ?fixedLengthString(classElement.entityValue().getValue()) : "?",
                    gradeelement != null ?fixedLengthString(gradeelement.entityValue().getValue()) : "?",
                    teacherElement != null ? fixedLengthString(teacherElement.entityValue().getValue()) : "?",
                    scheduleEvent, day, hour);
        }

        public MyLayout getForTime() {
            return prepareCellLayout("",hour,"","", null,null,null);
        }
        public MyLayout getForMonday() {
            return getForKey(MONDAY);
        }
        public MyLayout getForTuesday() {
            return getForKey(TUESDAY);
        }
        public MyLayout getForWednesday() {
            return getForKey(WEDNESDAY);
        }
        public MyLayout getForThursday() {
            return getForKey(THURSDAY);
        }
        public MyLayout getForFriday() {
            return getForKey(FRIDAY);
        }
    }

    public class MyLayout extends VerticalLayout {
        private ScheduleEvent ref;
        private String day, hour;

        public MyLayout(final ScheduleEvent ref, String day, String hour) {
            this.ref = ref;
            this.day = day;
            this.hour = hour;
        }
    }



}
