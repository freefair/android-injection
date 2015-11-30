package io.freefair.android.injection.modules.realm;

import android.content.Context;

import io.freefair.android.injection.InjectionProvider;
import io.freefair.android.injection.Injector;
import io.freefair.android.util.logging.AndroidLogger;
import io.realm.Realm;

public class ContextRealmProvider extends BaseRealmProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
        Context context = injector.resolveValue(Context.class, instance);
        return (T) Realm.getInstance(context);
    }
}
