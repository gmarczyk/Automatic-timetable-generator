package com.configuration;

import com.scheduler.interfaces.web.UserLoggingPage;
import com.scheduler.interfaces.web.classrooms.ClassroomGridView;
import com.scheduler.interfaces.web.condition.ConditionsGridView;
import com.scheduler.interfaces.web.grades.GradesGridView;
import com.scheduler.interfaces.web.schedule.CurrentScheduleView;
import com.scheduler.interfaces.web.scheduleevents.ScheduleEventsView;
import com.scheduler.interfaces.web.subjects.SubjectsGridView;
import com.scheduler.interfaces.web.teachers.TeachersGridView;
import com.scheduler.interfaces.web.users.UsersGridView;
import com.scheduler.shared.core.ResultCallback;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MainUIView {
//
    private final UI uiRef;
    private final MenuBar menuBar;
    private final MenuBar accountBar;
    private final Label unameLabel;

    private VerticalLayout mainLayout;

    public MainUIView(final UI ui) {
        this.uiRef = ui;
        this.menuBar = prepareMenuBar();
        this.accountBar = prepareAccountBar();
        this.unameLabel = prepareUsernameLabel();

        setMainContent(null);
    }

    public void addWindow(Window window) {
        this.uiRef.addWindow(window);
    }

    public void setMainContent(Component content) {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSizeFull();

        HorizontalLayout menulay = new HorizontalLayout();
        menulay.setSizeFull();

        menulay.addComponent(menuBar);
        menulay.addComponent(accountBar);
        menulay.addComponent(unameLabel);
        menulay.setExpandRatio(menuBar,8.65f);
        menulay.setExpandRatio(accountBar,0.6f);
        menulay.setExpandRatio(unameLabel,0.75f);

        mainLayout.addComponent(menulay);
        mainLayout.setExpandRatio(menulay,0.5f);

        if(content != null) {
            content.setSizeFull();
            content.addStyleName("mainContentBorder");
            this.mainLayout.addComponent(content);
            this.mainLayout.setExpandRatio(content,9.5f);
        }

        this.uiRef.setContent(mainLayout);
    }

    public void refresh() {

    }


    private MenuBar prepareMenuBar() {
        MenuBar result = new MenuBar();
        result.setWidth(100, Sizeable.Unit.PERCENTAGE);
        result.setHeightUndefined();
        result.addStyleName("toolbarShadow");

        MenuBar.MenuItem teachers = result.addItem("Nauczyciele", FontAwesome.MALE, new TeachersCommand());
        MenuBar.MenuItem rooms = result.addItem("Sale", FontAwesome.COMPASS, new RoomsCommand());
        MenuBar.MenuItem subjects = result.addItem("Przedmioty", FontAwesome.BOOK, new SubjectsCommand());
        MenuBar.MenuItem grades = result.addItem("Klasy", FontAwesome.USERS, new GradesCommand());
        MenuBar.MenuItem conditions = result.addItem("Warunki generacji", FontAwesome.HAND_O_RIGHT, new ConditionsCommand());

        MenuBar.MenuItem plany = result.addItem("Plany", FontAwesome.LIST, null);
        plany.addItem("Zdarzenia", FontAwesome.TABLE, new ScheduleEventsMenuCommand());
        plany.addItem("Aktualny plan", FontAwesome.BUILDING, new CurrentScheduleCommand());


        return result;
    }

    private MenuBar prepareAccountBar() {
        MenuBar result = new MenuBar();
        result.setWidth(100, Sizeable.Unit.PERCENTAGE);
        result.setHeightUndefined();
        result.addStyleName("toolbarShadow");

        MenuBar.MenuItem accountItem = result.addItem("Konto", FontAwesome.COG, null);
        accountItem.addItem("Wyloguj",FontAwesome.SIGN_OUT, new LogoutAccountCommand());
        accountItem.addItem("Uzytkownicy", FontAwesome.USERS, new UsersAccountCommand());

        return result;
    }

    private Label prepareUsernameLabel() {
        Label label= new Label();
        label.setSizeFull();
        return label;
    }

    public void showLoginView() {
        UserLoggingPage loginView = new UserLoggingPage(new ResultCallback<String>() {
            @Override
            public void callback(final String uname) {
                VerticalLayout vr=new VerticalLayout();
                vr.setSizeFull();
                MainUIView.this.setUsername(uname);
                MainUIView.this.setMainContent(vr);
            }
        });

        this.uiRef.setContent(loginView.getContent());
    }

    public void setUsername(String uname) {
        this.unameLabel.setValue(uname);
    }

    private class LogoutAccountCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            MainUIView.this.uiRef.getSession().close();
            MainUIView.this.uiRef.getPage().setLocation("/app");
        }
    }

    private class UsersAccountCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new UsersGridView(MainUIView.this);
        }
    }

    private class TeachersCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
           new TeachersGridView(MainUIView.this);
        }
    }

    private class RoomsCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new ClassroomGridView(MainUIView.this);
        }
    }

    private class SubjectsCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new SubjectsGridView(MainUIView.this);
        }
    }

    private class GradesCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new GradesGridView(MainUIView.this);
        }
    }

    private class ConditionsCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new ConditionsGridView(MainUIView.this);
        }
    }

    private class ScheduleEventsMenuCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new ScheduleEventsView(MainUIView.this);
        }
    }
    private class CurrentScheduleCommand implements MenuBar.Command {
        @Override
        public void menuSelected(final MenuBar.MenuItem menuItem) {
            new CurrentScheduleView(MainUIView.this);
        }
    }



}
