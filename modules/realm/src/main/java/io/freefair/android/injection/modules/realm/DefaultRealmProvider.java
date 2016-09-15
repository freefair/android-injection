package io.freefair.android.injection.modules.realm;

import io.freefair.injection.injector.Injector;
import io.realm.Realm;

public class DefaultRealmProvider extends BaseRealmProvider {

    @Override
    protected Realm createRealm(Object instance, Injector injector) {
        return Realm.getDefaultInstance();
    }
}
