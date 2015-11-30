package io.freefair.android.injection.modules;

import io.freefair.android.injection.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.modules.logging.AndroidLoggerProvider;

@SuppressWarnings("unused")
public class AndroidLoggerModule implements InjectionModule {

    @Override
    public void configure(InjectionContainer injectionContainer) {
        injectionContainer.registerProvider(new AndroidLoggerProvider());
    }
}
