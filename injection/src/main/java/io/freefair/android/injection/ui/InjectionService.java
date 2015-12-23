package io.freefair.android.injection.ui;

import android.app.Service;

import io.freefair.android.injection.Injector;
import io.freefair.android.injection.InjectorProvider;
import io.freefair.android.injection.platform.ServiceInjector;

/**
 * A {@link Service} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionService extends Service implements InjectorProvider {

    private ServiceInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();

        Injector parentInjector = null;
        if (getApplication() instanceof InjectionApplication) {
            parentInjector = ((InjectionApplication) getApplication()).getInjector();
        }
        injector = new ServiceInjector(parentInjector, this);
        injector.inject(this);
    }

    @Override
    public Injector getInjector() {
        return injector;
    }
}
