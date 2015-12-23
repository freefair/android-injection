package io.freefair.android.injection.injector;

import android.app.Service;

import io.freefair.android.injection.helper.RClassHelper;

public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Injector parentInjector, Service service) {
        super(parentInjector, service, RClassHelper.getRClassFromService(service));
    }
}
