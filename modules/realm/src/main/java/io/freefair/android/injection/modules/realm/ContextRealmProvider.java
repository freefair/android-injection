package io.freefair.android.injection.modules.realm;

import android.content.Context;

import io.freefair.android.injection.injector.Injector;
import io.realm.Realm;

public class ContextRealmProvider extends BaseRealmProvider {

    @Override
    protected Realm createRealm(Object instance, Injector injector) {
        Context context = injector.resolveValue(Context.class, instance);
        return Realm.getInstance(context);
    }
}
