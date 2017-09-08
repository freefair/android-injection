package io.freefair.android.injection.injector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

import io.freefair.android.injection.annotation.Inject;
import io.freefair.android.injection.provider.BeanProvider;
import io.freefair.util.function.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

        beanProviders.addLast(new NewInstanceProvider());
    }

    public void register(BeanProvider beanProvider) {
        beanProviders.addFirst(beanProvider);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<? extends T> resolveBean(@NotNull Class<T> type, Object instance) {

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

    @Slf4j
    private static class NewInstanceProvider implements BeanProvider {

        @Override
        public boolean canProvideBean(Class<?> type) {
            if (type.isPrimitive() || type.isAnnotation() || type.isArray() || type.isEnum() || type.isInterface()) {
                return false;
            }

            if (Modifier.isAbstract(type.getModifiers())) {
                return false;
            }

            try {
                type.newInstance();
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {

            T newInstance = null;
            try {
                newInstance = (T) clazz.newInstance();
            } catch (Exception e) {
                //Look for constructor annotated with @Inject
                for (Constructor<?> constructor : clazz.getConstructors()) {
                    if (constructor.isAnnotationPresent(Inject.class)) {

                        //resolve constructor params;
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        Object[] parameterValues = new Object[parameterTypes.length];
                        for (int i = 0; i < parameterTypes.length; i++) {
                            parameterValues[i] = injector.resolveBean(parameterTypes[i], null).orNull();
                        }

                        try {
                            newInstance = (T) constructor.newInstance(parameterValues);
                        } catch (Exception e1) {
                            NewInstanceProvider.log.error("Error while calling constructor " + constructor.toString(), e1);
                        }
                    }
                }
            }
            if (newInstance != null) {
                injector.inject(newInstance);
            }
            return newInstance;
        }
    }
}
