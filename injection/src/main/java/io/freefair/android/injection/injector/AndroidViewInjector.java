package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Field;
import java.util.Map;

import io.freefair.android.injection.annotation.InjectView;
import io.freefair.android.injection.helper.Bindings;

public abstract class AndroidViewInjector<T> extends AndroidResourceInjector<T> {

    public AndroidViewInjector(T object, Object... possibleParents) {
        super(object, possibleParents);
    }

    @Override
    protected void inject(@NonNull Object instance, @NonNull Field field) {
        if (field.isAnnotationPresent(InjectView.class)) {
            InjectView injectViewAnnotation = field.getAnnotation(InjectView.class);
            Bindings.getViewBinding(getObjectClass()).put(field, injectViewAnnotation.value());
        } else {
            super.inject(instance, field);
        }
    }

    public void injectViews() {
        for (Map.Entry<Field, Integer> viewBinding : Bindings.getViewBinding(getObjectClass()).entrySet()) {
            View view = findViewById(viewBinding.getValue());
            inject(viewBinding.getKey(), view);
        }
    }

    protected abstract View findViewById(@IdRes int viewId);
}
