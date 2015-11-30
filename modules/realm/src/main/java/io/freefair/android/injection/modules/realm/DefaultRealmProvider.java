package io.freefair.android.injection.modules.realm;

import io.freefair.android.injection.Injector;
import io.realm.Realm;

public class DefaultRealmProvider extends BaseRealmProvider {

    @Override
    public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) Realm.getDefaultInstance();
    }
}
