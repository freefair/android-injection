package io.freefair.injection;

import io.freefair.injection.injector.Injector;

public interface InjectionProvider {
    boolean canProvide(Class<?> clazz);

    <T> T provide(Class<? super T> clazz, Object instance, Injector injector);
}
