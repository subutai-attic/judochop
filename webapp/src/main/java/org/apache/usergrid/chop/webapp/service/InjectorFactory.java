package org.apache.usergrid.chop.webapp.service;

import com.google.inject.Injector;

// Temp fix: can't get Vaadin UI injection
public class InjectorFactory {

    private static Injector INJECTOR;

    public static void setInjector(Injector injector) {
        INJECTOR = injector;
    }

    public static <T> T getInstance(Class<T> c) {
        return INJECTOR.getInstance(c);
    }
}

