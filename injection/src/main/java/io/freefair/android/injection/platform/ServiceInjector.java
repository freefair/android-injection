package io.freefair.android.injection.platform;

import android.app.Service;

import io.freefair.android.injection.Injector;
import io.freefair.android.injection.helper.RClassHelper;

public class ServiceInjector extends AndroidResourceInjector<Service> {
    public ServiceInjector(Injector parentInjector, Service service) {
        super(parentInjector, service, RClassHelper.getRClassFromService(service));
    }
}
