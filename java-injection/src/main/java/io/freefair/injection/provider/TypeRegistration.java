package io.freefair.injection.provider;

import io.freefair.injection.injector.Injector;

public class TypeRegistration<IMPL extends IFACE, IFACE> implements BeanProvider {

    private final Class<IMPL> implClass;
    private final Class<IFACE> iFace;

    public TypeRegistration(Class<IMPL> implClass, Class<IFACE> iFace) {
        this.implClass = implClass;
        this.iFace = iFace;
    }

    @Override
    public boolean canProvideBean(Class<?> type) {
        return type.isAssignableFrom(iFace);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) injector.resolveBean(implClass, instance).orNull();
    }
}
