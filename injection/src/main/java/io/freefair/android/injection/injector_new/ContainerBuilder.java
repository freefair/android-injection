package io.freefair.android.injection.injector_new;

import java.util.HashSet;
import java.util.Set;

import io.freefair.android.injection.InjectionModule;

public class ContainerBuilder
{
	private Set<ContainerRegistrationBuilder<?>> registrations = new HashSet<>();

	public <TType> ContainerRegistrationBuilder<TType> register(Class<TType> cls) {
		ContainerRegistrationBuilder<TType> tTypeContainerRegistrationBuilder = new ContainerRegistrationBuilder<>(cls);
		registrations.add(tTypeContainerRegistrationBuilder);
		return tTypeContainerRegistrationBuilder;
	}

	public Container build() {
		Set<ContainerRegistration> finalRegistrations = new HashSet<>();
		for(ContainerRegistrationBuilder<?> registration : registrations) {
			finalRegistrations.add(registration.build());
		}
		return new Container(finalRegistrations);
	}
}
