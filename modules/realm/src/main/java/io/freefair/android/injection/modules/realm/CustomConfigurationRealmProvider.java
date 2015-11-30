package io.freefair.android.injection.modules.realm;

import io.freefair.android.injection.Injector;
import io.freefair.android.util.function.Supplier;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CustomConfigurationRealmProvider extends BaseRealmProvider {

    private Supplier<RealmConfiguration> realmConfiguration;

    public CustomConfigurationRealmProvider(Supplier<RealmConfiguration> realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    @Override
    protected Realm createRealm(Object instance, Injector injector) {
        return Realm.getInstance(realmConfiguration.get());
    }
}
