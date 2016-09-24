package io.freefair.injection.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import io.freefair.injection.annotation.Inject;
import io.freefair.injection.injector.Injector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewInstanceProvider implements BeanProvider {

    @Override
    public boolean canProvideBean(Class<?> type) {
        if (type.isPrimitive() || type.isAnnotation() || type.isArray() || type.isEnum() || type.isInterface())
            return false;

        if (Modifier.isAbstract(type.getModifiers()))
            return false;

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
                        log.error("Error while calling constructor " + constructor.toString(), e1);
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
