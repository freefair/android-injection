package io.freefair.android.injection.injector;

import android.app.IntentService;
import android.content.Context;

public class IntentServiceInjector extends AndroidResourceInjector<IntentService> {

    public IntentServiceInjector(IntentService object, Object... possibleParents) {
        super(object, possibleParents);
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject();
    }
}
