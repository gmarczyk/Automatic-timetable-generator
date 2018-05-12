package com.scheduler.presentation.subjects;

import com.scheduler.presentation.framework.EditableViewElement;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class SubjectEditableViewElement implements EditableViewElement {

    private HorizontalLayout layout;

    private TextField name = new TextField("Nazwa");

    public SubjectEditableViewElement() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponents(name);

        name.setSizeFull();

        layout.setComponentAlignment(name, Alignment.TOP_CENTER);
    }

    public TextField getName() {
        return name;
    }


    @Override
    public Component getEditableLayout() {
        return layout;
    }
}
