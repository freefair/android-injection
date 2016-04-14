package io.freefair.android.injection.injector_new;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainerRegistrationBuilder <TType> {
	private Class<TType> cls;
	private Set<Class<?>> ifaces;
	private Scope scope = Scope.Object;
	private Map<String, Object> parameters = new HashMap<>();

	public ContainerRegistrationBuilder(Class<TType> cls) {
		this.cls = cls;
	}

	public ContainerRegistrationBuilder<TType> as(Class<?> iface) {
		if(ifaces == null) ifaces = new HashSet<>();
		ifaces.add(iface);

		return this;
	}

	public ContainerRegistrationBuilder<TType> inSingletonScope() {
		scope = Scope.Singleton;
		return this;
	}

	public ContainerRegistrationBuilder<TType> inActivityScope() {
		scope = Scope.Activity;
		return this;
	}

	public ContainerRegistrationBuilder<TType> inClassScope() {
		scope = Scope.Class;
		return this;
	}

	public ContainerRegistrationBuilder<TType> withConstructorParameter(String parameterName, Object parameter) {
		parameters.put(parameterName, parameter);
		return this;
	}

	ContainerRegistration build() {
		ContainerRegistration containerRegistration = new ContainerRegistration();
		containerRegistration.setMainClass(cls);
		containerRegistration.setInterfaces(ifaces);
		containerRegistration.setScope(scope);
		containerRegistration.setConstructorParameters(parameters);
		return containerRegistration;
	}
}
