package com.scheduler.interfaces.web.teachers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;
import com.scheduler.application.teacher.TeacherService;
import com.scheduler.domain.teacher.Teacher;
import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;

@Configurable(preConstruction = true)
public class TeachersGridView implements RefreshableLayout {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LoginService loginService;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<Teacher> gridView;

    public TeachersGridView(MainUIView mv) {
        this.mv = mv;

        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }

    private void setupGridView() {

        this.gridView = new GenericGridViewWithBasicManagement<Teacher>(teacherService.allTeachers(), mv) {
            @Override
            public void configureGrid() {
                this.grid.addColumn(Teacher::getFirstName).setCaption("Imie");
                this.grid.addColumn(Teacher::getLastName).setCaption("Nazwisko");
                this.grid.addColumn(Teacher::getShortcut).setCaption("Skrot");

                if (!loginService.isAnyOfRoles(UserRole.TENANT_ADMIN, UserRole.MANAGEMENT)) {
                    this.addEditDeleteContent.setEnabled(false);
                }
            }

            @Override
            public void configureManagementButtons() {
                this.addEditDeleteContent.getLayout().addComponent(prepareRefreshButton());

                this.addEditDeleteContent.getAddButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        AddTeacherWindow addTeacherWindow = new AddTeacherWindow(mv);
                        addTeacherWindow.show();
                    }
                });

                this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        Set<Teacher> selectedItems = grid.getSelectedItems();
                        if(selectedItems.size() != 1) {
                            return;
                        }

                        teacherService.delete(Iterables.getOnlyElement(selectedItems));
                    }
                });
            }
        };
    }

    private Button prepareRefreshButton() {
        final Button refreshButton = new Button("Odswiez");
        refreshButton.setSizeFull();
        refreshButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                TeachersGridView.this.refreshLay();
            }
        });

        return refreshButton;
    }

    @Override
    public void refreshLay() {
        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }
}

