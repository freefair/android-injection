package io.freefair.android.injection.injector;

import android.app.Service;
import android.content.Context;

/**
 * @author Lars Grefer
 */
public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Service service, Object... possibleParents) {
        super(service, possibleParents);
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject();
    }
}
