package io.freefair.android.injection.modules.realm;

import io.freefair.android.injection.InjectionProvider;
import io.freefair.android.injection.Injector;
import io.realm.Realm;

abstract class BaseRealmProvider implements InjectionProvider {
    @Override
    public boolean canProvide(Class<?> clazz) {
        return clazz.isAssignableFrom(Realm.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) createRealm(instance, injector);
    }

    protected abstract Realm createRealm(Object instance, Injector injector);
}
