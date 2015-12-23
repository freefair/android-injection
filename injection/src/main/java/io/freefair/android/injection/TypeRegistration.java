package io.freefair.android.injection;

import io.freefair.android.injection.injector.Injector;

public class TypeRegistration<IMPL extends IFACE, IFACE> implements InjectionProvider {

	private final Class<IMPL> implClass;
	private final Class<IFACE> iFace;

	public TypeRegistration(Class<IMPL> implClass, Class<IFACE> iFace){
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
