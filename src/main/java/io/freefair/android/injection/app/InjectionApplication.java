package io.freefair.android.injection.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import io.freefair.android.injection.injector.ApplicationInjector;
import io.freefair.android.injection.injector.RuntimeInjector;
import io.freefair.android.injection.provider.BeanProviders;
import io.freefair.android.injection.provider.InjectorProvider;
import io.freefair.util.function.Supplier;

/**
 * An {@link Application} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionApplication extends Application implements InjectorProvider {

    private RuntimeInjector runtimeInjector = RuntimeInjector.getInstance();
    private ApplicationInjector applicationInjector;

    @Override
    public void onCreate() {
        applicationInjector = new ApplicationInjector(this);
        super.onCreate();

        runtimeInjector.register(BeanProviders.ofSupplier(Context.class, new Supplier<Context>() {
            @Override
            public Context get() {
                return getApplicationContext();
            }
        }));

        applicationInjector.inject(this);
        injectAttributesAndResources();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        injectAttributesAndResources();
    }

    private void injectAttributesAndResources() {
        if (applicationInjector != null) {
            applicationInjector.injectResources();
            applicationInjector.injectAttributes();
        }
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        injectAttributesAndResources();
    }

    public ApplicationInjector getInjector() {
        return applicationInjector;
    }
}
