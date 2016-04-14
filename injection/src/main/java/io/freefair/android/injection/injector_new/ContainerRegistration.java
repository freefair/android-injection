package io.freefair.android.injection.injector_new;

import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class ContainerRegistration {
	private Class<?> mainClass;
	private Set<Class<?>> interfaces;
	private Scope scope;
	private Map<String, Object> constructorParameters;
}
