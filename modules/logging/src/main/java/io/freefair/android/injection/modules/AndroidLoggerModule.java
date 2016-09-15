package io.freefair.android.injection.modules;

import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.injection.InjectionModule;
import io.freefair.android.injection.modules.logging.AndroidLoggerProvider;

@SuppressWarnings("unused")
public class AndroidLoggerModule implements InjectionModule {

    @Override
    public void configure(RuntimeInjector runtimeInjector) {
        runtimeInjector.registerProvider(new AndroidLoggerProvider());
    }
}
