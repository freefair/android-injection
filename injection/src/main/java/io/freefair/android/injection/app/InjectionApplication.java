package io.freefair.android.injection.app;

import android.app.Application;

import io.freefair.android.injection.injector.ApplicationInjector;
import io.freefair.injection.InjectionModule;
import io.freefair.injection.InjectorProvider;
import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.util.function.Suppliers;

/**
 * An {@link Application} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionApplication extends Application implements InjectorProvider {

    private RuntimeInjector runtimeInjector = RuntimeInjector.getInstance();
    private ApplicationInjector applicationInjector;

    public InjectionApplication() {
        runtimeInjector.registerSupplier(InjectionApplication.class, Suppliers.of(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationInjector = new ApplicationInjector(this);

        applicationInjector.inject(this);
    }

    public ApplicationInjector getInjector() {
        return applicationInjector;
    }

    public void addModule(InjectionModule injectionModule) {
        injectionModule.configure(runtimeInjector);
    }
}
