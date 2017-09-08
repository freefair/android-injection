package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.freefair.android.injection.annotation.InjectView;

abstract class AndroidViewInjector<T> extends AndroidResourceInjector<T> {

    AndroidViewInjector(T object, Object... possibleParents) {
        super(object, possibleParents);
    }

    @Override
    protected void visitField(@NonNull Object instance, @NonNull FieldWrapper field) {
        if (field.isAnnotationPresent(InjectView.class)) {
            InjectView injectViewAnnotation = field.getAnnotation(InjectView.class);
            Bindings.getViewBinding(getObjectClass()).put(field, injectViewAnnotation.value());
        }
        super.visitField(instance, field);
    }

    public void injectViews() {
        for (Map.Entry<FieldWrapper, Integer> viewBinding : Bindings.getViewBinding(getObjectClass()).entrySet()) {
            View view = findViewById(viewBinding.getValue());
            viewBinding.getKey().set(getObject(), view);
        }
    }

    protected abstract View findViewById(@IdRes int viewId);

    private static class Bindings {
        private static WeakHashMap<Class<?>, Map<FieldWrapper, Integer>> viewBindings = new WeakHashMap<>();

        @NonNull
        static Map<FieldWrapper, Integer> getViewBinding(Class<?> clazz) {
            if (!viewBindings.containsKey(clazz)) {
                viewBindings.put(clazz, new HashMap<FieldWrapper, Integer>());
            }
            return viewBindings.get(clazz);
        }
    }
}
