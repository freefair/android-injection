package io.freefair.android.injection.injector;

import android.app.Service;
import android.content.Context;

public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Service service, Object... possibleParents) {
        super(service, possibleParents);
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject();
    }
}
