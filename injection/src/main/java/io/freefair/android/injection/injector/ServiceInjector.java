package io.freefair.android.injection.injector;

import android.app.Service;

public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Injector parentInjector, Service service) {
        super(parentInjector, service);
    }
}
