package io.freefair.android.injection.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import io.freefair.android.injection.injector.ApplicationInjector;
import io.freefair.injection.InjectionModule;
import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.injection.provider.InjectorProvider;
import io.freefair.injection.provider.SupplierProvider;
import io.freefair.util.function.Suppliers;

/**
 * An {@link Application} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionApplication extends Application implements InjectorProvider {

    private RuntimeInjector runtimeInjector = RuntimeInjector.getInstance();
    private ApplicationInjector applicationInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        runtimeInjector.registerBeanProvider(new SupplierProvider<>(Context.class, Suppliers.of(this.getApplicationContext())));

        applicationInjector = new ApplicationInjector(this);

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

    public void addModule(InjectionModule injectionModule) {
        runtimeInjector.registerModule(injectionModule);
    }
}
