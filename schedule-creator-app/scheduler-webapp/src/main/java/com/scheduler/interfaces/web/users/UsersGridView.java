package com.scheduler.interfaces.web.users;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.configuration.MainUIView;
import com.google.common.collect.Iterables;
import com.scheduler.application.LoginService;
import com.scheduler.presentation.framework.GenericGridViewWithBasicManagement;
import com.scheduler.presentation.framework.RefreshableLayout;
import com.scheduler.shared.users.domain.users.User;
import com.scheduler.shared.users.domain.users.UserRepository;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.ui.Button;

@Configurable(preConstruction = true)
public class UsersGridView implements RefreshableLayout {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginService loginService;

    private final MainUIView mv;
    private GenericGridViewWithBasicManagement<User> gridView;

    public UsersGridView(MainUIView mv) {
        this.mv = mv;

        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }

    private void setupGridView() {

        this.gridView = new GenericGridViewWithBasicManagement<User>(userRepository.list(), mv) {

            @Override
            public void configureGrid() {
                this.grid.addColumn(User::getUsername).setCaption("Nazwa");
                this.grid.addColumn(User::getRole).setCaption("Typ");

                if(!loginService.isAnyOfRoles(UserRole.TENANT_ADMIN)) {
                    this.addEditDeleteContent.setEnabled(false);
                }
            }

            @Override
            public void configureManagementButtons() {
                this.addEditDeleteContent.getLayout().addComponent(prepareRefreshButton());

                this.addEditDeleteContent.getAddButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        AddUserWindow addUserWindow = new AddUserWindow(mv);
                        addUserWindow.show();
                    }
                });

                this.addEditDeleteContent.getLayout().removeComponent(this.addEditDeleteContent.getEditButton());

                this.addEditDeleteContent.getDeleteButton().addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent clickEvent) {
                        Set<User> selectedItems = grid.getSelectedItems();
                        if(selectedItems.size() != 1) {
                            return;
                        }

                        userRepository.delete(Iterables.getOnlyElement(selectedItems));
                    }
                });
            }
        };

    }

    @Override
    public void refreshLay() {
        setupGridView();
        mv.setMainContent(this.gridView.getContent());
    }

    private Button prepareRefreshButton() {
        final Button refreshButton = new Button("Odswiez");
        refreshButton.setSizeFull();
        refreshButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                UsersGridView.this.refreshLay();
            }
        });

      return refreshButton;
    }


}
