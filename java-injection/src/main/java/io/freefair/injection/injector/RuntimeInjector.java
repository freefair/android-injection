package io.freefair.injection.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.freefair.injection.InjectionProvider;
import io.freefair.injection.annotation.Inject;
import io.freefair.injection.exceptions.InjectionException;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Supplier;

public class RuntimeInjector extends Injector {

    private static RuntimeInjector instance;

    public static RuntimeInjector getInstance() {
        if (instance == null) {
            instance = new RuntimeInjector();
        }
        return instance;
    }

    private Map<Class<?>, Supplier<?>> injectionSupplier;
    private Set<InjectionProvider> injectionFactories;

    private RuntimeInjector() {
        super(null);
        injectionSupplier = new HashMap<>();
        injectionFactories = new HashSet<>();
    }

    @SuppressWarnings("unused")
    public <IMPL extends IFACE, IFACE> void registerType(Class<IMPL> impl, final Class<IFACE> iFace) {
        this.registerProvider(new TypeRegistration<>(impl, iFace));
    }

    @SuppressWarnings("unused")
    public <T> void registerSupplier(Class<T> type, Supplier<? extends T> supplier) {
        injectionSupplier.put(type, supplier);
    }

    @SuppressWarnings("unused")
    public void registerProvider(InjectionProvider injectionProvider) {
        injectionFactories.add(injectionProvider);
    }

    @Override
    protected void inject(@NotNull Object instance, @NotNull Field field) {
        if (field.isAnnotationPresent(Inject.class)) {
            Inject injectAnnotation = field.getAnnotation(Inject.class);

            Class<?> targetType = injectAnnotation.value().equals(Object.class)
                    ? resolveTargetType(field)
                    : injectAnnotation.value();

            Object value = getInjector(instance).resolveValue(targetType, instance);

            if (value == null) {
                if (!injectAnnotation.optional() && !field.getType().equals(Optional.class)) {
                    throw new InjectionException("Unable to resolve value of type " + targetType.toString() + " for Field " + field.toString());
                }
            }

            inject(instance, field, value);
        } else {
            super.inject(instance, field);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T resolveValue(@NotNull Class<T> type, Object instance) {
        if (type.isAssignableFrom(Injector.class)) {
            return (T) this;
        }

        if (type.isAnnotation()) {
            Class<? extends Annotation> annotationType = (Class<? extends Annotation>) type;
            return (T) instance.getClass().getAnnotation(annotationType);
        }

        Optional<T> supplierValue = querySupplier(type);
        if (supplierValue.isPresent())
            return supplierValue.get();

        Optional<T> factoryValue = queryFactories(type, instance);
        if (factoryValue.isPresent())
            return factoryValue.get();

        return super.resolveValue(type, instance);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> querySupplier(Class<T> type) {

        Supplier<T> supplier = (Supplier<T>) injectionSupplier.get(type);

        if (supplier == null) {
            for (Map.Entry<Class<?>, Supplier<?>> supplierEntry : injectionSupplier.entrySet()) {
                if (type.isAssignableFrom(supplierEntry.getKey())) {
                    supplier = (Supplier<T>) supplierEntry.getValue();
                }
            }
        }

        if (supplier != null) {
            T value = supplier.get();
            inject(value);
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> queryFactories(Class<T> type, Object instance) {
        for (InjectionProvider factory : injectionFactories) {
            if (factory.canProvide(type))
                return Optional.of(factory.provide(type, instance, this));
        }
        return Optional.empty();
    }

    public static class TypeRegistration<IMPL extends IFACE, IFACE> implements InjectionProvider {

        private final Class<IMPL> implClass;
        private final Class<IFACE> iFace;

        public TypeRegistration(Class<IMPL> implClass, Class<IFACE> iFace) {
            this.implClass = implClass;
            this.iFace = iFace;
        }

        @Override
        public boolean canProvide(Class<?> clazz) {
            return clazz.isAssignableFrom(iFace);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
            return (T) injector.resolveValue(implClass, instance);
        }
    }
}
