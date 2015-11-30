package io.freefair.android.injection.modules;

import io.freefair.android.injection.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.modules.realm.DefaultRealmProvider;

/**
 * Created by larsgrefer on 30.11.15.
 */
public class DefaultRealmModule extends RealmModule implements InjectionModule {

    @Override
    public void configure(InjectionContainer injectionContainer) {
        injectionContainer.registerProvider(new DefaultRealmProvider());
    }
}
