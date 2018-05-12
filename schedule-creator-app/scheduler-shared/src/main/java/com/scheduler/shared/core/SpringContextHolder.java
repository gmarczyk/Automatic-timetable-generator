package com.scheduler.shared.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextHolder {

    private AnnotationConfigApplicationContext ctx;

    private static SpringContextHolder instance = null;
    public static synchronized SpringContextHolder instance() {
        if (instance == null) {
            instance = new SpringContextHolder();
        }
        return instance;
    }

    private SpringContextHolder() {
        // empty
    }

    public void setContext(AnnotationConfigApplicationContext ctx) {
        this.ctx = ctx;
    }

    public AnnotationConfigApplicationContext getCtx() {
        return ctx;
    }
}
