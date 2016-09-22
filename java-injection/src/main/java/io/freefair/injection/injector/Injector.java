package io.freefair.injection.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.WeakHashMap;

import io.freefair.injection.annotation.Inject;
import io.freefair.injection.annotation.Value;
import io.freefair.injection.exceptions.InjectionException;
import io.freefair.injection.reflection.Reflection;
import io.freefair.util.function.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstact implementation of a dependency injector
 */
@Slf4j
public abstract class Injector {

    private final Optional<Injector> parentInjector;

    public Injector(Object... parentInjectors) {
        if (parentInjectors == null) {
            this.parentInjector = Optional.empty();
        } else {
            this.parentInjector = Optional.of(InjectorUtils.getParentInjector(parentInjectors));
        }
        topClasses = new HashSet<>();
    }

    private WeakHashMap<Object, Class<?>> alreadyInjectedInstances = new WeakHashMap<>();
    private static WeakHashMap<Object, Injector> responsibleInjectors = new WeakHashMap<>();

    protected Injector getInjector(Object instance) {
        Injector injector = responsibleInjectors.get(instance);
        if (injector != null)
            return injector;
        return this;
    }

    /**
     * Injects as much as possible into the given object
     *
     * @param instance The object to inject into
     */
    public final void inject(@NotNull Object instance) {
        inject(instance, instance.getClass());
    }

    Deque<Object> instancesStack = new LinkedList<>();

    public final void inject(@NotNull Object instance, @NotNull Class<?> clazz) {
        responsibleInjectors.put(instance, this);
        if (!alreadyInjectedInstances.containsKey(instance)) {
            long start = System.currentTimeMillis();

            instancesStack.addLast(instance);
            alreadyInjectedInstances.put(instance, clazz);
            for (Field field : Reflection.getAllFields(clazz, getUpToExcluding(clazz))) {
                visitField(instance, FieldWrapper.of(field));
            }
            instancesStack.removeLast();

            long end = System.currentTimeMillis();
            log.debug("Injection of " + instance + " took " + (end - start) + "ms");
        }
    }

    @Getter(PROTECTED)
    private Set<Class<?>> topClasses;

    @NotNull
    @SuppressWarnings("unchecked")
    private <X> Class<X> getUpToExcluding(Class<? extends X> clazz) {
        for (Class<?> topClazz : topClasses) {
            if (topClazz.isAssignableFrom(clazz))
                return (Class<X>) topClazz;
        }
        return (Class<X>) Object.class;
    }

    /**
     * Inject the field, or call super
     *
     * @param instance the instance to inject into
     * @param field    the field to inject
     */
    protected void visitField(@NotNull Object instance, @NotNull FieldWrapper field) {
        if (field.isAnnotationPresent(Inject.class)) {
            Inject injectAnnotation = field.getAnnotation(Inject.class);

            Class<?> targetType = injectAnnotation.value().equals(Object.class)
                    ? field.getType()
                    : injectAnnotation.value();

            Optional<?> bean = getInjector(instance).resolveBean(targetType, instance);

            field.set(instance, bean.orNull());
        }

        if (field.isAnnotationPresent(Value.class)) {
            Value valueAnnotation = field.getAnnotation(Value.class);

            Class<?> targetType = field.getType();

            Optional<?> value = getInjector(instance).resolveValue(valueAnnotation.value(), targetType);

            field.set(instance, value.orNull());
        }

        if (parentInjector.isPresent()) {
            parentInjector.get().visitField(instance, field);
        }
    }

    /**
     * Resolve the given type to an object, or call super
     * <p/>
     * The base implementation asks the parent if possible or tries to provide a new instance
     *
     * @param <T>      the type of the object to return
     * @param type     the type of the object to return
     * @param instance the instance the returned object will be injected into
     * @return The object to use for the given type
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <T> Optional<? extends T> resolveBean(@NotNull Class<T> type, @Nullable Object instance) {
        for (Object inst : instancesStack) {
            if (type.isInstance(inst))
                return Optional.of((T) inst);
        }

        if (parentInjector.isPresent()) {
            return parentInjector.get().resolveBean(type, instance);
        } else {
            try {
                return Optional.ofNullable(createNewInstance(type, instance));
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }

    @NotNull
    public <V> Optional<V> resolveValue(String key, Class<V> type) {
        if (parentInjector.isPresent())
            return parentInjector.get().resolveValue(key, type);
        else
            return Optional.empty();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T createNewInstance(@NotNull Class<T> type, Object instance) {

        T newInstance = null;
        try {
            newInstance = type.newInstance();
        } catch (Exception e) {
            //Look for constructor annotated with @Inject
            for (Constructor<?> constructor : type.getConstructors()) {
                if (constructor.isAnnotationPresent(Inject.class)) {

                    //resolve constructor params;
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] parameterValues = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        parameterValues[i] = getInjector(instance).resolveBean(parameterTypes[i], null).orNull();
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
            getInjector(instance).inject(newInstance);
        }
        return newInstance;
    }

    protected static class FieldWrapper {
        private static WeakHashMap<Field, FieldWrapper> cache = new WeakHashMap<>();

        @Getter
        private final Field field;

        @Getter
        private final Class<?> type;

        @Getter
        @Setter
        private boolean optional;

        private FieldWrapper(Field field) {
            this.field = field;

            this.type = resolveType();

            if(field.isAnnotationPresent(io.freefair.injection.annotation.Optional.class))
                optional = true;
        }

        private Class<?> resolveType() {
            if (field.getType().equals(Optional.class)) {
                setOptional(true);
                return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            }

            if (field.getType().equals(WeakReference.class))
                return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            return field.getType();
        }

        public static FieldWrapper of(Field field) {
            if (cache.containsKey(field)) {
                return cache.get(field);
            }

            FieldWrapper fieldWrapper = new FieldWrapper(field);

            cache.put(field, fieldWrapper);
            return fieldWrapper;
        }

        public void set(Object instance, Object value) {

            if(value == null && !isOptional()){
                throw new InjectionException("No value for required field " + field.toString());
            }
            try {
                field.setAccessible(true);
            } catch (SecurityException ignored) {

            }

            if (field.getType().equals(Optional.class))
                value = Optional.ofNullable(value);

            if (field.getType().equals(WeakReference.class))
                value = new WeakReference<>(value);

            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                log.error("Cannot inject value", e);
                throw new InjectionException(e);
            }
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
            return getField().isAnnotationPresent(annotationClass);
        }

        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return getField().getAnnotation(annotationClass);
        }
    }
}
