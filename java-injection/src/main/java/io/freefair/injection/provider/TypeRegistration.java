package io.freefair.injection.provider;

import io.freefair.injection.injector.Injector;

public class TypeRegistration<IMPL extends IFACE, IFACE> implements BeanProvider {

    private final Class<IMPL> implementationClass;
    private final Class<IFACE> interfaceClass;

    public TypeRegistration(Class<IMPL> implementationClass, Class<IFACE> interfaceClass) {
        this.implementationClass = implementationClass;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public boolean canProvideBean(Class<?> type) {
        return type.isAssignableFrom(interfaceClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
        return (T) injector.resolveBean(implementationClass, instance).orNull();
    }
}
