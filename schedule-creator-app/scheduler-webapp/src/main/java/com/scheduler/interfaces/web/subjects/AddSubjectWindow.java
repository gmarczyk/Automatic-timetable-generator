package com.scheduler.interfaces.web.subjects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.domain.subjects.events.SubjectCreatedEvent;
import com.scheduler.domain.subjects.SubjectRepository;

import com.scheduler.presentation.framework.GenericCreateObjectContent;
import com.scheduler.presentation.subjects.SubjectEditableViewElement;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

@Configurable
public class AddSubjectWindow {

    @Autowired
    SubjectRepository subjectRepository;

    private final MainUIView mv;

    private Window window;
    private GenericCreateObjectContent<SubjectEditableViewElement> subjectCreateContent;

    public AddSubjectWindow(MainUIView mv) {
        this.mv = mv;

        this.window = new Window("Dodaj przedmiot");
        this.window.setWidth(30, Sizeable.Unit.PERCENTAGE);

        initSubjectCreateContent();
        window.setContent(subjectCreateContent.getContent());
        window.center();
    }

    private void initSubjectCreateContent() {
        this.subjectCreateContent = new GenericCreateObjectContent<SubjectEditableViewElement>() {

            @Override
            public SubjectEditableViewElement getNewObject() {
                return new SubjectEditableViewElement();
            }

            @Override
            public Button.ClickListener initCreateButtonListener() {
                return new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        for (final SubjectEditableViewElement item : subjectCreateContent.getItems()) {
                            subjectRepository.create(new SubjectCreatedEvent(item.getName().getValue()));
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
