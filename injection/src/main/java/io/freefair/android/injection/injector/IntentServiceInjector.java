package io.freefair.android.injection.injector;

import android.app.IntentService;

public class IntentServiceInjector extends AndroidResourceInjector<IntentService> {

    public IntentServiceInjector(IntentService object, Object... possibleParents) {
        super(object, possibleParents);
    }
}
