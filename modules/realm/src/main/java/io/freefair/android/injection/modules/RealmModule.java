package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import io.freefair.android.injection.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.modules.realm.ContextRealmProvider;
import io.freefair.android.injection.modules.realm.CustomConfigurationRealmProvider;
import io.freefair.android.injection.modules.realm.DefaultRealmProvider;
import io.freefair.android.util.function.Supplier;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * An {@link InjectionModule} which enables the injection of {@link Realm Realms}
 *
 * @see <a href="https://realm.io/docs/java/latest/">https://realm.io/docs/java/latest/</a>
 */
@SuppressWarnings("unused")
public abstract class RealmModule implements InjectionModule {

    /**
     * @return a {@link RealmModule} which uses {@link Realm#getDefaultInstance()}
     * @see Realm#getDefaultInstance()
     */
    @NonNull
    public static RealmModule usingDefaultConfig() {
        return new RealmModule() {
            @Override
            public void configure(InjectionContainer injectionContainer) {
                injectionContainer.registerProvider(new DefaultRealmProvider());
            }
        };
    }

    /**
     * @return a {@link RealmModule} which uses {@link Realm#getInstance(Context)}
     * @see Realm#getInstance(Context)
     */
    @NonNull
    public static RealmModule usingContext() {
        return new RealmModule() {
            @Override
            public void configure(InjectionContainer injectionContainer) {
                injectionContainer.registerProvider(new ContextRealmProvider());
            }
        };
    }

    /**
     * @param realmConfiguration the {@link RealmConfiguration} to use for creating realms
     * @return a {@link RealmModule} which uses {@link Realm#getInstance(RealmConfiguration)}
     * @see io.realm.Realm#getInstance(RealmConfiguration)
     */
    @NonNull
    public static RealmModule usingCustomConfiguration(final Supplier<RealmConfiguration> realmConfiguration) {
        return new RealmModule() {
            @Override
            public void configure(InjectionContainer injectionContainer) {
                injectionContainer.registerProvider(new CustomConfigurationRealmProvider(realmConfiguration));
            }
        };
    }
}
