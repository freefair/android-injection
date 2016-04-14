package io.freefair.android.injection.injector_new;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ScopeTree
{
	private final Scope scope;
	private WeakHashMap<Class<?>, Object> scopeObjects = new WeakHashMap<>();
	private ScopeTree parent;
	private List<ScopeTree> children = new ArrayList<>();

	public void destroy() {
		scopeObjects.clear();
		for (ScopeTree child : children) {
			child.destroy();
		}
		children.clear();
		parent = null;
	}
}
