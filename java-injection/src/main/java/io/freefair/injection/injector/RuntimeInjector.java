package io.freefair.injection.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.provider.SupplierProvider;
import io.freefair.injection.provider.TypeRegistration;
import io.freefair.injection.provider.ValueProvider;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

public class RuntimeInjector extends Injector {

    private static RuntimeInjector instance;

    public static RuntimeInjector getInstance() {
        if (instance == null) {
            instance = new RuntimeInjector();
        }
        return instance;
    }

    @Getter
    @Setter
    private Properties properties = new Properties();
    private Deque<BeanProvider> beanProviders = new ArrayDeque<>();
    private Deque<ValueProvider> valueProviders = new ArrayDeque<>();

    private RuntimeInjector() {
        super(null);

        valueProviders.addLast(new PropertiesValueProvider());
        valueProviders.addLast(new SystemPropertiesValueProvider());
        valueProviders.addLast(new EnvValueProvider());
    }

    public void registerModule(InjectionModule injectionModule) {
        Optional<? extends BeanProvider> beanProvider = injectionModule.getBeanProvider();
        if (beanProvider.isPresent()) {
            registerBeanProvider(beanProvider.get());
        }

        Optional<? extends ValueProvider> valueProvider = injectionModule.getValueProvider();
        if (valueProvider.isPresent()) {
            registerValueProvider(valueProvider.get());
        }
    }

    @Deprecated
    public <IMPL extends IFACE, IFACE> void registerType(Class<IMPL> impl, final Class<IFACE> iFace) {
        this.registerBeanProvider(new TypeRegistration<>(impl, iFace));
    }

    @Deprecated
    public <T> void registerSupplier(Class<T> type, Supplier<? extends T> supplier) {
        this.registerBeanProvider(new SupplierProvider<>(type, supplier));
    }

    public void registerBeanProvider(BeanProvider beanProvider) {
        beanProviders.addFirst(beanProvider);
    }

    public void registerValueProvider(ValueProvider valueProvider) {
        valueProviders.addFirst(valueProvider);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T resolveBean(@NotNull Class<T> type, Object instance) {
        if (type.isAssignableFrom(Injector.class)) {
            return (T) this;
        }

        if (type.isAnnotation()) {
            Class<? extends Annotation> annotationType = (Class<? extends Annotation>) type;
            return (T) instance.getClass().getAnnotation(annotationType);
        }

        T value = null;
        for (BeanProvider beanProvider : beanProviders) {
            if (beanProvider.canProvideBean(type)) {
                value = beanProvider.provideBean(type, instance, this);
                if (value != null)
                    break;
            }
        }

        if (value != null)
            return value;

        return super.resolveBean(type, instance);
    }

    @Override
    public <V> Optional<V> resolveValue(String key, Class<V> type) {

        for (ValueProvider valueProvider : valueProviders) {
            if (valueProvider.canProvideValue(key, type)) {
                return Optional.of(valueProvider.provideValue(key, type));
            }
        }

        return super.resolveValue(key, type);
    }

    private class PropertiesValueProvider implements ValueProvider {

        @Override
        public boolean canProvideValue(String key, Class<?> type) {
            return properties.containsKey(key) && type.isInstance(properties.get(key));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V> V provideValue(String key, Class<? super V> type) {
            return (V) properties.get(key);
        }
    }

    private static class SystemPropertiesValueProvider implements ValueProvider {

        @Override
        public boolean canProvideValue(String key, Class<?> type) {
            if (type.isAssignableFrom(String.class)) {
                try {
                    String property = System.getProperty(key);
                    if (property != null) return true;
                } catch (SecurityException e) {
                    return false;
                }
            } else {
                Properties properties;
                try {
                    properties = System.getProperties();
                } catch (SecurityException e) {
                    return false;
                }
                if (properties.containsKey(key)) {
                    Object property = properties.get(key);
                    return type.isInstance(property);
                } else {
                    return false;
                }
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V> V provideValue(String key, Class<? super V> type) {
            if (type.isAssignableFrom(String.class)) {
                return (V) System.getProperty(key);
            } else {
                return (V) System.getProperties().get(key);
            }
        }
    }

    private static class EnvValueProvider implements ValueProvider {
        @Override
        public boolean canProvideValue(String key, Class<?> type) {
            if (type.isAssignableFrom(String.class)) {
                try {
                    return System.getenv(key) != null;
                } catch (SecurityException e) {
                    return false;
                }
            }

            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V> V provideValue(String key, Class<? super V> type) {
            return (V) System.getenv(key);
        }
    }
}
