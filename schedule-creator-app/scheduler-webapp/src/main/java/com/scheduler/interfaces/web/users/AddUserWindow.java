package com.scheduler.interfaces.web.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.scheduler.application.LoginService;
import com.scheduler.presentation.framework.GenericCreateObjectContent;
import com.scheduler.presentation.users.UserEditableViewElement;
import com.scheduler.shared.event.application.EventPublisher;
import com.scheduler.shared.users.domain.users.UserRegisteredEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

@Configurable
public class AddUserWindow {

    @Autowired
    private LoginService loginService;

    private final MainUIView mv;

    private Window window;
    private GenericCreateObjectContent<UserEditableViewElement> userCreateContent;

    public AddUserWindow(MainUIView mv) {
        this.mv = mv;

        this.window = new Window("Dodaj uzytkownika");
        this.window.setWidth(30, Sizeable.Unit.PERCENTAGE);

        initUserCreateContent();
        window.setContent(userCreateContent.getContent());
        window.center();
    }

    private void initUserCreateContent() {
        this.userCreateContent = new GenericCreateObjectContent<UserEditableViewElement>(false) {

            @Override
            public UserEditableViewElement getNewObject() {
                return new UserEditableViewElement();
            }

            @Override
            public Button.ClickListener initCreateButtonListener() {
                return new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        for (final UserEditableViewElement item : userCreateContent.getItems()) {
                            EventPublisher.publish(new UserRegisteredEvent(item.getUsername().getValue(),
                                    item.getPassword().getValue(), item.getRoleComboBox().getSelectedItem().get()));
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
