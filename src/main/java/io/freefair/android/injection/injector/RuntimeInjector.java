package io.freefair.android.injection.injector;

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

import io.freefair.android.injection.provider.BeanProvider;
import io.freefair.util.function.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lars Grefer
 */
public final class RuntimeInjector extends Injector {

    private static RuntimeInjector runtimeInjector;

    public static RuntimeInjector getInstance() {
        if (runtimeInjector == null) {
            runtimeInjector = new RuntimeInjector();
        }
        return runtimeInjector;
    }

    @Getter
    @Setter
    private Properties properties = new Properties();
    private Deque<BeanProvider> beanProviders = new ArrayDeque<>();

    private RuntimeInjector() {
        super((Object[]) null);
    }

    public void register(BeanProvider beanProvider) {
        beanProviders.addFirst(beanProvider);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<? extends T> resolveBean(@NonNull Class<T> type, Object instance) {

        Optional<? extends T> optional = super.resolveBean(type, instance);
        if (optional.isPresent()) {
            return optional;
        }

        if (type.isAnnotation()) {
            Class<? extends Annotation> annotationType = (Class<? extends Annotation>) type;
            return Optional.ofNullable((T) instance.getClass().getAnnotation(annotationType));
        }

        T value = null;
        for (BeanProvider beanProvider : beanProviders) {
            if (beanProvider.canProvideBean(type)) {
                value = beanProvider.provideBean(type, instance, getInjector(instance));
                if (value != null) {
                    break;
                }
            }
        }

        return Optional.ofNullable(value);
    }

}
