package io.freefair.android.injection.injector;

import android.app.Service;

public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Service service, Object... possibleParents) {
        super(service, possibleParents);
    }
}
