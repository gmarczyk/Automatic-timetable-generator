package com.scheduler.interfaces.web.grades;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;
import com.scheduler.domain.grades.Grade;
import com.scheduler.domain.grades.GradeRepository;

import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;

@Configurable(preConstruction = true)
public class GradesGridView implements RefreshableLayout {

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private LoginService loginService;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<Grade> gridView;

    public GradesGridView (MainUIView mv) {
        this.mv = mv;

        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }

    private void setupGridView() {

        this.gridView = new GenericGridViewWithBasicManagement<Grade>(gradeRepository.allGrades(), mv) {
            @Override
            public void configureGrid() {
                this.grid.addColumn(Grade::getGradeSymbol).setCaption("Symbol");

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
                        AddGradeWindow addGradeWindow= new AddGradeWindow(mv);
                        addGradeWindow.show();
                    }
                });

                this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        Set<Grade> selectedItems = grid.getSelectedItems();
                        if(selectedItems.size() != 1) {
                            return;
                        }

                        gradeRepository.delete(Iterables.getOnlyElement(selectedItems));
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
                GradesGridView.this.refreshLay();
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