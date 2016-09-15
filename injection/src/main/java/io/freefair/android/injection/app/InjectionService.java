package io.freefair.android.injection.app;

import android.app.Service;
import android.content.res.Configuration;

import io.freefair.android.injection.injector.ServiceInjector;
import io.freefair.injection.InjectorProvider;

/**
 * A {@link Service} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionService extends Service implements InjectorProvider {

    private ServiceInjector serviceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        serviceInjector = new ServiceInjector(this, getApplication());
        serviceInjector.inject(this);
        serviceInjector.injectAttributes();
        serviceInjector.injectResources();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (serviceInjector != null) {
            serviceInjector.injectAttributes();
            serviceInjector.injectResources();
        }
    }

    @Override
    public ServiceInjector getInjector() {
        return serviceInjector;
    }
}
