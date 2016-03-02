package io.freefair.android.injection.helper;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.freefair.android.injection.annotation.InjectAttribute;
import io.freefair.android.injection.annotation.InjectResource;

/**
 * Storage for Bindings between Fields and {@link android.view.View Views}, Fields and Attributes and Fields and Resources
 */
public class Bindings {

    private static WeakHashMap<Class<?>, Map<Field, InjectAttribute>> attributeBindings = new WeakHashMap<>();
    private static WeakHashMap<Class<?>, Map<Field, InjectResource>> resourceBindings = new WeakHashMap<>();
    private static WeakHashMap<Class<?>, Map<Field, Integer>> viewBindings = new WeakHashMap<>();

    @NonNull
    public static Map<Field, InjectAttribute> getAttributeBinding(Class<?> clazz) {
        if (!attributeBindings.containsKey(clazz))
            attributeBindings.put(clazz, new HashMap<Field, InjectAttribute>());
        return attributeBindings.get(clazz);
    }

    @NonNull
    public static Map<Field, InjectResource> getResourceBinding(Class<?> clazz) {
        if (!resourceBindings.containsKey(clazz))
            resourceBindings.put(clazz, new HashMap<Field, InjectResource>());
        return resourceBindings.get(clazz);
    }

    @NonNull
    public static Map<Field, Integer> getViewBinding(Class<?> clazz) {
        if (!viewBindings.containsKey(clazz))
            viewBindings.put(clazz, new HashMap<Field, Integer>());
        return viewBindings.get(clazz);
    }

}
