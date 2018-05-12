package com.configuration.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import com.configuration.AppStartingIoCContainter;
import com.configuration.HandlerRegistrar;
import com.scheduler.shared.core.SpringContextHolder;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;

import com.scheduler.shared.event.infrastructure.RabbitConnection;
import com.vaadin.ui.UI;

/**
 * This MainUI is the application entry point. A MainUI may either represent a browser window
 * (or tab) or testEventData part of a html page where a Vaadin application is embedded.
 * <p>
 * The MainUI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add components to the user interface and initialize non-components functionality.
 */
@Theme("mytheme")
@PreserveOnRefresh
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        AnnotationConfigApplicationContext ctx = SpringContextHolder.instance().getCtx();
        ctx.getBean(AppStartingIoCContainter.class).startApplication(this);
    }

    @WebServlet(value = {"/app/*", "/VAADIN/*"}, name = "MainUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    public static class MainUIServlet extends VaadinServlet {
        // main servlet
    }

    @WebServlet(urlPatterns = "/initServlet", name = "InitServlet", asyncSupported = true, loadOnStartup = 1)
    @VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
    @Configuration
    @ComponentScan({"com.scheduler", "com.configuration"})
    @EnableSpringConfigured
    @EnableLoadTimeWeaving(aspectjWeaving=EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
    public static class InitServlet extends VaadinServlet {

        @Override
        public void init(ServletConfig servletConfig) {
            AnnotationConfigApplicationContext cfg = new AnnotationConfigApplicationContext(this.getClass());
            SpringContextHolder.instance().setContext(cfg);

            RabbitConnection rabbitConnection = RabbitConnection.getConnection();
            new HandlerRegistrar().registerHandlers(rabbitConnection);
        }

    }

}
