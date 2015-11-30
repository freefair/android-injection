package io.freefair.android.injection.modules.realm;

import io.freefair.android.injection.InjectionProvider;
import io.realm.Realm;

public abstract class BaseRealmProvider implements InjectionProvider {
    @Override
    public boolean canProvide(Class<?> clazz) {
        return clazz.isAssignableFrom(Realm.class);
    }
}
