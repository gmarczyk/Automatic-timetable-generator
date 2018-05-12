package com.scheduler.presentation.classrooms;

import com.scheduler.presentation.framework.EditableViewElement;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class ClassroomEditableViewElement implements EditableViewElement {

    private HorizontalLayout layout;

    private TextField classroomCode = new TextField("Numer klasy");

    public ClassroomEditableViewElement() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponents(classroomCode);

        classroomCode.setSizeFull();

        layout.setComponentAlignment(classroomCode, Alignment.TOP_CENTER);
    }

    public TextField getClassroomCode() {
        return classroomCode;
    }


    @Override
    public Component getEditableLayout() {
        return layout;
    }
}
