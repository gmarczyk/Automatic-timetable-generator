package com.scheduler.presentation.users;

import com.scheduler.presentation.framework.EditableViewElement;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class UserEditableViewElement implements EditableViewElement {

    private HorizontalLayout layout;

    private TextField username = new TextField("Nazwa uzytkownika");
    private PasswordField password = new PasswordField("Haslo");
    private ComboBox<UserRole> roleComboBox = new ComboBox<>("Typ uzytkownika");

    public UserEditableViewElement() {
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponents(username, password, roleComboBox);

        username.setSizeFull();
        password.setSizeFull();
        roleComboBox.setSizeFull();
        roleComboBox.setItems(UserRole.values());
        roleComboBox.setValue(UserRole.TEACHER);

        layout.setComponentAlignment(roleComboBox, Alignment.TOP_CENTER);
        layout.setComponentAlignment(username, Alignment.TOP_CENTER);
        layout.setComponentAlignment(password, Alignment.TOP_CENTER);
    }

    public TextField getUsername() {
        return username;
    }

    public TextField getPassword() {
        return password;
    }

    public ComboBox<UserRole> getRoleComboBox() {
        return roleComboBox;
    }

    @Override
    public Component getEditableLayout() {
        return layout;
    }
}
