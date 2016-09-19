package io.freefair.android.injection.modules.realm;

import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.injector.Injector;
import io.realm.Realm;

abstract class BaseRealmProvider implements BeanProvider {
    @Override
    public boolean canProvideBean(Class<?> type) {
        return type.isAssignableFrom(Realm.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) createRealm(instance, injector);
    }

    protected abstract Realm createRealm(Object instance, Injector injector);
}
