package com.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.scheduler.application.LoginService;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;
import com.configuration.servlet.MainUI;
import com.scheduler.shared.users.domain.users.User;

@Component
public class AppStartingIoCContainter {

    @Autowired
    LoginService loginService;
    @Autowired
    GenericTenantAwareHibernateRepository<User> tenantAwareHibernateRepository;

    private MainUI mainUI;
    private MainUIView mv;

    public void startApplication(final MainUI mainUI) {
        this.mainUI = mainUI;
        this.mv = new MainUIView(mainUI);
        loginService.setSessionHoldingUI(mainUI);


        mv.showLoginView();
    }



}
