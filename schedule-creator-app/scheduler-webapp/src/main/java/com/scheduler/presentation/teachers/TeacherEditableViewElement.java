package com.scheduler.presentation.teachers;



import com.scheduler.presentation.framework.EditableViewElement;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class TeacherEditableViewElement implements EditableViewElement {

    private HorizontalLayout layout;

    private TextField name = new TextField("Imie");
    private TextField surname = new TextField("Nazwisko");

    public TeacherEditableViewElement() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponents(name, surname);

        name.setSizeFull();
        surname.setSizeFull();

        layout.setComponentAlignment(name, Alignment.TOP_CENTER);
        layout.setComponentAlignment(surname, Alignment.TOP_CENTER);
    }

    public TextField getName() {
        return name;
    }

    public TextField getSurname() {
        return surname;
    }

    @Override
    public Component getEditableLayout() {
        return layout;
    }
}
