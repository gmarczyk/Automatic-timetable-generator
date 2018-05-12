package com.configuration.servlet;

import javax.servlet.annotation.WebServlet;

import com.scheduler.application.LoginService;
import com.scheduler.infrastructure.multitenancy.TenantRepositoryUNAWARE;
import com.scheduler.presentation.framework.LoginPanel;
import com.scheduler.shared.core.SpringContextHolder;
import com.scheduler.shared.users.domain.multitenancy.Tenant;
import com.scheduler.shared.users.domain.multitenancy.TenantId;
import com.scheduler.shared.users.domain.users.UserRole;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class InternalPanelUI extends UI {

    private static final String ADMIN_LOGIN = "admin@a.pl";
    private static final String ADMIN_HASH = "$2a$12$hSOwoF5OVq/.A6xldO4lpOMnnpRsZXGn6pNtUjIl8ZznJwtzM4o1W";

    @WebServlet(value = {"/internalPanel/*"}, name = "InternalPanelServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = InternalPanelUI.class, productionMode = false)
    public static class InternalPanelServlet extends VaadinServlet {
        // main servlet
    }

    @Override
    protected void init(final VaadinRequest vaadinRequest) {

        LoginPanel loginPanel = new LoginPanel("Panel superadministratora");
        loginPanel.setLogInBackendListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                if(loginPanel.username().equals(ADMIN_LOGIN) &&
                  (LoginService.bcrypt_checkPassword(loginPanel.password(), ADMIN_HASH))) {

                    runInternalPanel();
                }
                else {
                    Notification.show("Error", Notification.Type.WARNING_MESSAGE);
                }
            }

        });

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponents(loginPanel);
        layout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

        this.setContent(layout);
    }

    private void runInternalPanel() {
        VerticalLayout internalPanelLayout = new VerticalLayout();
        internalPanelLayout.setSizeFull();
        Panel panel = prepareTenantRegistrationPanel();
        internalPanelLayout.addComponent(panel);
        internalPanelLayout.setComponentAlignment(panel,Alignment.MIDDLE_CENTER);
        this.setContent(internalPanelLayout);
    }

    private Panel prepareTenantRegistrationPanel() {
        Panel panel = new Panel("Rejestracja tenanta");
        TextField tenantName = new TextField("Nazwa tenanta");
        TextField username = new TextField("Login admina tenanta");
        PasswordField passwordField = new PasswordField("Haslo admina tenanta");
        Button register = new Button("Rejestruj");

        VerticalLayout layout= new VerticalLayout();
        layout.setSizeUndefined();
        layout.addComponents(tenantName,username,passwordField,register);

        register.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                if(username.getValue() != null && passwordField.getValue() != null && tenantName.getValue() != null) {

                    LoginService loginService = SpringContextHolder.instance().getCtx().getBean(LoginService.class);
                    TenantRepositoryUNAWARE tenantRepo = SpringContextHolder.instance().getCtx().getBean(
                            TenantRepositoryUNAWARE.class);

                    if(tenantRepo.findByName(tenantName.getValue()) != null) {
                        Notification.show("Taki tenant juz istnieje", Notification.Type.ERROR_MESSAGE);
                        return;
                    }

                    Tenant newTenant = new Tenant();
                    newTenant.setTenantName(tenantName.getValue());
                    tenantRepo.create(newTenant);

                    if(loginService.tryRegisteringNewTenantAdminUser(username.getValue(),passwordField.getValue(),
                            new TenantId(newTenant.getId()), UserRole.TENANT_ADMIN)) {
                        Notification.show("Tenant zarejestrowany", Notification.Type.HUMANIZED_MESSAGE);
                    }
                    else  {
                        Notification.show("Blad podczas rejestracji", Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.setSizeUndefined();
        panel.setContent(layout);
        return panel;
    }

}
