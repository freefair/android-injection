package io.freefair.android.injection.app;

import android.app.Application;

import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.InjectorProvider;
import io.freefair.android.util.function.Suppliers;

/**
 * An {@link Application} with support for dependency injection
 */
@SuppressWarnings("unused")
public class InjectionApplication extends Application implements InjectorProvider {

    private InjectionContainer injector = InjectionContainer.getInstance();

    public InjectionApplication() {
        injector.registerSupplier(InjectionApplication.class, Suppliers.of(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injector.inject(this);
    }

    public InjectionContainer getInjector() {
        return injector;
    }

    public void addModule(InjectionModule injectionModule) {
        injectionModule.configure(getInjector());
    }
}
