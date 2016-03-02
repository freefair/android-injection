package io.freefair.android.injection;

import io.freefair.android.injection.injector.Injector;

public interface InjectionProvider {
    boolean canProvide(Class<?> clazz);

    <T> T provide(Class<? super T> clazz, Object instance, Injector injector);
}
