package io.freefair.android.injection.app;

import android.app.Application;

import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.InjectorProvider;
import io.freefair.android.util.function.Suppliers;

/**
 * An {@link Application} with support for dependency injection
 */
@SuppressWarnings("unused")
public class InjectionApplication extends Application implements InjectorProvider {

    private InjectionContainer injector;

    public InjectionApplication() {
        injector = InjectionContainer.getInstance();
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
}
