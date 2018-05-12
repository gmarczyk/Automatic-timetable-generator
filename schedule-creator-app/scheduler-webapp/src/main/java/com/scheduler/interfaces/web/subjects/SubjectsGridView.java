package com.scheduler.interfaces.web.subjects;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;

import com.scheduler.domain.subjects.Subject;
import com.scheduler.domain.subjects.SubjectRepository;

import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;

@Configurable(preConstruction = true)
public class SubjectsGridView implements RefreshableLayout {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private LoginService loginService;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<Subject> gridView;

    public SubjectsGridView(MainUIView mv) {
        this.mv = mv;

        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }

    private void setupGridView() {

        this.gridView = new GenericGridViewWithBasicManagement<Subject>(subjectRepository.allSubjects(), mv) {
            @Override
            public void configureGrid() {
                this.grid.addColumn(Subject::getSubjectName).setCaption("Nazwa");

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
                        AddSubjectWindow addSubjectWindow = new AddSubjectWindow(mv);
                        addSubjectWindow.show();
                    }
                });

                this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        Set<Subject> selectedItems = grid.getSelectedItems();
                        if(selectedItems.size() != 1) {
                            return;
                        }

                        subjectRepository.delete(Iterables.getOnlyElement(selectedItems));
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
                SubjectsGridView.this.refreshLay();
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