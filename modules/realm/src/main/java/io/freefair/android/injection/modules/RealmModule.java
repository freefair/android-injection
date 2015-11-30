package io.freefair.android.injection.modules;

/**
 * Created by larsgrefer on 30.11.15.
 */
public abstract class RealmModule {

    public static RealmModule withDefaultConfig() {
        return new DefaultRealmModule();
    }
}
