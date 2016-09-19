package io.freefair.android.injection.modules;

import android.support.annotation.NonNull;

import io.freefair.android.injection.modules.realm.CustomConfigurationRealmProvider;
import io.freefair.android.injection.modules.realm.DefaultRealmProvider;
import io.freefair.injection.InjectionModule;
import io.freefair.injection.InjectionModuleBase;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Supplier;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * An {@link InjectionModule} which enables the injection of {@link Realm Realms}
 *
 * @see <a href="https://realm.io/docs/java/latest/">https://realm.io/docs/java/latest/</a>
 */
@SuppressWarnings("unused")
public abstract class RealmModule extends InjectionModuleBase {

    /**
     * @return a {@link RealmModule} which uses {@link Realm#getDefaultInstance()}
     * @see Realm#getDefaultInstance()
     */
    @NonNull
    public static RealmModule usingDefaultConfig() {
        return new RealmModule() {
            @Override
            public Optional<DefaultRealmProvider> getBeanProvider() {
                return Optional.of(new DefaultRealmProvider());
            }
        };
    }

    /**
     * @param realmConfiguration the {@link RealmConfiguration} to use for creating realms
     * @return a {@link RealmModule} which uses {@link Realm#getInstance(RealmConfiguration)}
     * @see io.realm.Realm#getInstance(RealmConfiguration)
     */
    @NonNull
    public static RealmModule usingCustomConfiguration(@NonNull final Supplier<RealmConfiguration> realmConfiguration) {
        return new RealmModule() {

            @Override
            public Optional<CustomConfigurationRealmProvider> getBeanProvider() {
                return Optional.of(new CustomConfigurationRealmProvider(realmConfiguration));
            }
        };
    }
}
