package com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DI {
    public static Injector INJECTOR = Guice.createInjector();

    public static void install(Module module) {
        synchronized (DI.class) {
            INJECTOR = Guice.createInjector(module);
        }
    }

    public static Injector di() {
        return INJECTOR;
    }
}
