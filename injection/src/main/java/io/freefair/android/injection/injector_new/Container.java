package io.freefair.android.injection.injector_new;

import android.app.Activity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import io.freefair.android.injection.annotation.Inject;

public class Container {
	private Set<ContainerRegistration> finalRegistrations;
	private ScopeTree scopeTree;

	public Container(Set<ContainerRegistration> finalRegistrations) {
		this.finalRegistrations = finalRegistrations;
		scopeTree = new ScopeTree(Scope.Singleton);
	}

	public void startScope(Scope scope) {
		ScopeTree c = new ScopeTree(scope);
		c.setParent(scopeTree);
		scopeTree.getChildren().add(c);
		scopeTree = c;
	}

	public void destroyScope(Scope scope) {
		ScopeTree c = scopeTree;
		while(c.getScope() != scope) {
			c = c.getParent();
		}
		c.destroy();
	}

	public void inject(Object object) {
		if(object == null) throw new IllegalArgumentException("object is null");
		try {
			Class<?> aClass = object.getClass();
			Field[] declaredFields = aClass.getDeclaredFields();
			for (Field declaredField : declaredFields) {
				Inject annotation = declaredField.getAnnotation(Inject.class);
				if(annotation == null) continue;
				declaredField.setAccessible(true);
				Object resolve = resolve(declaredField.getType());
				if(resolve == null && !annotation.optional()) throw new Exception("could not resolve type");
				else if(resolve == null) continue;
				declaredField.set(object, resolve);
			}
		}
		catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	public <TType extends TInterface, TInterface> TType resolve(Class<TInterface> interfaceClass) {
		return null;
	}
}
