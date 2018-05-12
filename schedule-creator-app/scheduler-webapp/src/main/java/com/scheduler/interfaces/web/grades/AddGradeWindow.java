package com.scheduler.interfaces.web.grades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.grades.events.GradeCreatedEvent;
import com.scheduler.domain.grades.GradeRepository;
import com.scheduler.presentation.framework.GenericCreateObjectContent;
import com.scheduler.presentation.grades.GradeEditableViewElement;
import com.scheduler.shared.event.application.EventPublisher;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;


public class AddGradeWindow {

    private final MainUIView mv;

    private Window window;
    private GenericCreateObjectContent<GradeEditableViewElement> gradeCreateContent;

    public AddGradeWindow(MainUIView mv) {
        this.mv = mv;

        this.window = new Window("Dodaj klase");
        this.window.setWidth(30, Sizeable.Unit.PERCENTAGE);

        initSubjectCreateContent();
        window.setContent(gradeCreateContent.getContent());
        window.center();
    }

    private void initSubjectCreateContent() {
        this.gradeCreateContent = new GenericCreateObjectContent<GradeEditableViewElement>() {

            @Override
            public GradeEditableViewElement getNewObject() {
                return new GradeEditableViewElement();
            }

            @Override
            public Button.ClickListener initCreateButtonListener() {
                return new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        for (final GradeEditableViewElement item : gradeCreateContent.getItems()) {
                            EventPublisher.publish(new GradeCreatedEvent(item.getGradeSymbol().getValue()));
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
