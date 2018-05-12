package com.scheduler.presentation.grades;

import com.scheduler.presentation.framework.EditableViewElement;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class GradeEditableViewElement implements EditableViewElement {

    private HorizontalLayout layout;

    private TextField gradeSymbol = new TextField("Symbol");

    public GradeEditableViewElement() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponents(gradeSymbol);

        gradeSymbol.setSizeFull();

        layout.setComponentAlignment(gradeSymbol, Alignment.TOP_CENTER);
    }

    public TextField getGradeSymbol() {
        return gradeSymbol;
    }


    @Override
    public Component getEditableLayout() {
        return layout;
    }
}