package io.freefair.android.injection.provider;

import io.freefair.android.injection.InjectionProvider;
import io.freefair.android.injection.Injector;
import io.freefair.android.util.logging.AndroidLogger;

public class AndroidLoggerProvider implements InjectionProvider {

    @Override
    public boolean canProvide(Class<?> clazz) {
        return clazz.isAssignableFrom(AndroidLogger.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) AndroidLogger.forObject(instance);
    }
}
