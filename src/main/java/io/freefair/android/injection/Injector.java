package io.freefair.android.injection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.WeakHashMap;

import io.freefair.android.injection.reflection.Reflection;
import io.freefair.android.util.Logger;
import io.freefair.android.injection.annotation.Inject;
import io.freefair.android.injection.exceptions.InjectionException;
import io.freefair.android.util.Optional;

/**
 * Abstact implementation of a dependency injector
 */
public abstract class Injector {

	private final Logger log;
	private Optional<Injector> parentInjector;

	public Injector(@Nullable Injector parentInjector) {
		log = Logger.forObject(this);
		this.parentInjector = Optional.ofNullable(parentInjector);
		alreadyInjectedInstances = new WeakHashMap<>();
	}

	private WeakHashMap<Object, Class<?>> alreadyInjectedInstances;

	/**
	 * Injects as much as possible into the given object
	 *
	 * @param instance The object to inject into
	 */
	public void inject(@NonNull Object instance) {
		inject(instance, instance.getClass());
	}

	public void inject(@NonNull Object instance, @NonNull Class<?> clazz) {
		if (!alreadyInjectedInstances.containsKey(instance)) {
			alreadyInjectedInstances.put(instance, clazz);
			for (Field field : Reflection.getAllFields(clazz, Object.class)) {
				inject(instance, field);
			}
		}
	}

	/**
	 * Inject the field, or call super
	 *
	 * @param instance the instance to inject into
	 * @param field    the field to inject
	 */
	protected void inject(@NonNull Object instance, @NonNull Field field) {
		if (parentInjector.isPresent()) {
			parentInjector.get().inject(instance, field);
		}
	}

	/**
	 * Resolve the given type to an object, or call super
	 * <p/>
	 * The base implementation asks the parent if possible or tries to provide a new instance
	 *
	 * @param type     the type of the object to return
	 * @param instance the instance the returned object will be injected into
	 * @param <T>      the type of the object to return
	 * @return The object to use for the given type
	 */
	@Nullable
	public <T> T resolveValue(@NonNull Class<T> type, @Nullable Object instance) {
		if (parentInjector.isPresent()) {
			T resolveValue = parentInjector.get().resolveValue(type, instance);
			inject(this);
			return resolveValue;
		} else {
			try {
				return createInjectedInstance(type);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Nullable
	protected <T> T createInjectedInstance(@NonNull Class<T> type) {
		T newInstance = createNewInstance(type);
		if (newInstance != null) {
			inject(newInstance);
		}
		return newInstance;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private <T> T createNewInstance(@NonNull Class<T> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			//Look for constructor annotated with @Inject
			for (Constructor<?> constructor : type.getConstructors()) {
				if (constructor.isAnnotationPresent(Inject.class)) {

					//resolve constructor params;
					Class<?>[] parameterTypes = constructor.getParameterTypes();
					Object[] parameterValues = new Object[parameterTypes.length];
					for (int i = 0; i < parameterTypes.length; i++) {
						parameterValues[i] = resolveValue(parameterTypes[i], null);
					}

					try {
						return (T) constructor.newInstance(parameterValues);
					} catch (Exception e1) {
						log.error("Error while calling constructor " + constructor.toString(), e1);
					}
				}
			}
		}
		return null;
	}

	/**
	 * resolves the actual type needed for the given field
	 * <p/>
	 * This method looks into {@link Optional}s and {@link WeakReference}s
	 * in order to resolve the real target type
	 *
	 * @return The type of the object, which is needed for the given field
	 */
	@NonNull
	protected Class<?> resolveTargetType(@NonNull Field field) {
		if (field.getType().equals(Optional.class))
			return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

		if (field.getType().equals(WeakReference.class))
			return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

		return field.getType();
	}

	/**
	 * Inject the given value into the given field in the given object.
	 * <p/>
	 * This method wraps the value into an {@link Optional} or {@link WeakReference}
	 * if necessary
	 *
	 * @param instance the instance to inject into
	 * @param field    the field to inject into
	 * @param value    the value to inject
	 */
	protected void inject(@NonNull Object instance, @NonNull Field field, @Nullable Object value) {
		field.setAccessible(true);

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
}
