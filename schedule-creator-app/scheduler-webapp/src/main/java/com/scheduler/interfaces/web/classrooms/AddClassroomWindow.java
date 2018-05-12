package com.scheduler.interfaces.web.classrooms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.classrooms.events.ClassroomCreatedEvent;
import com.scheduler.domain.classrooms.ClassroomRepository;
import com.scheduler.presentation.classrooms.ClassroomEditableViewElement;
import com.scheduler.presentation.framework.GenericCreateObjectContent;
import com.scheduler.shared.event.application.EventPublisher;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

public class AddClassroomWindow {

    private final MainUIView mv;

    private Window window;
    private GenericCreateObjectContent<ClassroomEditableViewElement> classroomCreateContent;

    public AddClassroomWindow(MainUIView mv) {
        this.mv = mv;

        this.window = new Window("Dodaj przedmiot");
        this.window.setWidth(30, Sizeable.Unit.PERCENTAGE);

        initClassroomCreateContent();
        window.setContent(classroomCreateContent.getContent());
        window.center();
    }

    private void initClassroomCreateContent() {
        this.classroomCreateContent = new GenericCreateObjectContent<ClassroomEditableViewElement>() {

            @Override
            public ClassroomEditableViewElement getNewObject() {
                return new ClassroomEditableViewElement();
            }

            @Override
            public Button.ClickListener initCreateButtonListener() {
                return new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        for (final ClassroomEditableViewElement item : classroomCreateContent.getItems()) {
                            EventPublisher.publish(new ClassroomCreatedEvent(item.getClassroomCode().getValue()));
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