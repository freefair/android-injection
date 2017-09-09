package io.freefair.android.injection.provider;

import io.freefair.android.injection.injector.Injector;

/**
 * @author Lars Grefer
 */
public interface BeanProvider {
    boolean canProvideBean(Class<?> type);

    <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector);

}
