package io.freefair.android.injection.injector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import io.freefair.android.injection.annotation.Inject;
import io.freefair.android.injection.annotation.Value;
import io.freefair.android.injection.exceptions.InjectionException;
import io.freefair.android.injection.reflection.Reflection;
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
            for (Field field : getFields(clazz)) {
                log.trace("Visit field {}", field);
                visitField(instance, FieldWrapper.of(field));
            }
            instancesStack.removeLast();

            long end = System.currentTimeMillis();
            log.debug("Injection of " + instance + " took " + (end - start) + "ms");
        }
    }

    private static WeakHashMap<Class<?>,List<Field>> fieldCache = new WeakHashMap<>();

    private List<Field> getFields(@NotNull Class<?> clazz) {
        if(!fieldCache.containsKey(clazz))
            fieldCache.put(clazz, Reflection.getAllFields(clazz, getUpToExcluding(clazz)));
        return fieldCache.get(clazz);
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

        if (type.isAssignableFrom(Injector.class)) {
            return Optional.of((T) this);
        }

        if (parentInjector.isPresent()) {
            return parentInjector.get().resolveBean(type, instance);
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public <V> Optional<V> resolveValue(String key, Class<V> type) {
        if (parentInjector.isPresent())
            return parentInjector.get().resolveValue(key, type);
        else
            return Optional.empty();
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

            if (field.isAnnotationPresent(io.freefair.android.injection.annotation.Optional.class))
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

            if (value == null && !isOptional()) {
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
