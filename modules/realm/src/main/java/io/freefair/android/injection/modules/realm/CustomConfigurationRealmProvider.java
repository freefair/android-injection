package io.freefair.android.injection.modules.realm;

import android.support.annotation.NonNull;

import io.freefair.injection.injector.Injector;
import io.freefair.util.function.Supplier;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CustomConfigurationRealmProvider extends BaseRealmProvider {

    @NonNull
    private Supplier<RealmConfiguration> realmConfiguration;

    public CustomConfigurationRealmProvider(@NonNull Supplier<RealmConfiguration> realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
    }

    @Override
    protected Realm createRealm(Object instance, Injector injector) {
        return Realm.getInstance(realmConfiguration.get());
    }
}
