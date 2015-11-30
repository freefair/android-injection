package io.freefair.android.injection.modules;

import io.freefair.android.injection.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.modules.realm.ContextRealmProvider;

@SuppressWarnings("unused")
public class ContextRealmModule implements InjectionModule {

    @Override
    public void configure(InjectionContainer injectionContainer) {
        injectionContainer.registerProvider(new ContextRealmProvider());
    }
}
