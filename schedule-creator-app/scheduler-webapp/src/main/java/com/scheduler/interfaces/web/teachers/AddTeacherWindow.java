package com.scheduler.interfaces.web.teachers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.application.teacher.TeacherService;
import com.scheduler.domain.teacher.events.TeacherCreatedEvent;
import com.scheduler.presentation.framework.GenericCreateObjectContent;
import com.scheduler.presentation.teachers.TeacherEditableViewElement;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

@Configurable
public class AddTeacherWindow {

    @Autowired
    TeacherService teacherService;

    private final MainUIView mv;

    private Window window;
    private GenericCreateObjectContent<TeacherEditableViewElement> teacherCreateContent;

    public AddTeacherWindow(MainUIView mv) {
        this.mv = mv;

        this.window = new Window("Dodaj nauczyciela");
        this.window.setWidth(30, Sizeable.Unit.PERCENTAGE);

        initTeacherCreateContent();
        window.setContent(teacherCreateContent.getContent());
        window.center();
    }

    private void initTeacherCreateContent() {
        this.teacherCreateContent = new GenericCreateObjectContent<TeacherEditableViewElement>() {

            @Override
            public TeacherEditableViewElement getNewObject() {
                return new TeacherEditableViewElement();
            }

            @Override
            public Button.ClickListener initCreateButtonListener() {
                return new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        for (final TeacherEditableViewElement item : teacherCreateContent.getItems()) {
                            teacherService.create(new TeacherCreatedEvent(item.getName().getValue(), item.getSurname().getValue()));
                        }

                        window.close();
                    }
                };
            }

        };
    }

    public void show() {
        mv.addWindow(window);
    }
}
