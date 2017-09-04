package io.freefair.injection.provider;

import io.freefair.injection.injector.Injector;

public interface BeanProvider {
    boolean canProvideBean(Class<?> type);

    <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector);

}
