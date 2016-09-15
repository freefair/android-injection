package io.freefair.injection;

import io.freefair.injection.injector.RuntimeInjector;

public interface InjectionModule {
    void configure(RuntimeInjector runtimeInjector);
}
