package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.freefair.android.injection.annotation.InjectView;
import io.freefair.android.injection.exceptions.ViewIdNotFoundException;
import io.freefair.android.util.logging.AndroidLogger;
import io.freefair.android.util.logging.Logger;

public abstract class AndroidViewInjector<T> extends AndroidResourceInjector<T> {

    private Logger log = AndroidLogger.forObject(this);

    public AndroidViewInjector(Injector parentInjector, T object, Class<?> rClass) {
        super(parentInjector, object, rClass);
    }

    @Override
    protected void inject(@NonNull Object instance, @NonNull Field field) {
        if (field.isAnnotationPresent(InjectView.class)) {
            getViewBinding().put(field, findViewId(field));
        } else {
            super.inject(instance, field);
        }
    }

    public void injectViews() {
        for (Map.Entry<Field, Integer> viewBinding : getViewBinding().entrySet()) {
            View view = findViewById(viewBinding.getValue());
            inject(viewBinding.getKey(), view);
        }
    }

    protected abstract View findViewById(@IdRes int viewId);

    @IdRes
    protected int findViewId(Field field) throws ViewIdNotFoundException {

        if (field.isAnnotationPresent(InjectView.class)) {
            InjectView injectViewAnnotation = field.getAnnotation(InjectView.class);
            if (injectViewAnnotation.value() != InjectView.DEFAULT_ID) {
                return injectViewAnnotation.value();
            }
        }

        String fieldName = field.getName();
        if (isRAvailable()) {
            try {
                return getRid().getDeclaredField(fieldName).getInt(null);
            } catch (NoSuchFieldException e) {
                log.info("Field " + fieldName + " not found in R class");
            } catch (IllegalAccessException e) { //that should never happen
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        throw new ViewIdNotFoundException(fieldName);
    }

    private Map<Field, Integer> getViewBinding() {
        if (!viewBindings.containsKey(getObjectClass())) {
            viewBindings.put(getObjectClass(), new HashMap<Field, Integer>());
        }
        return viewBindings.get(getObjectClass());
    }

    private static WeakHashMap<Class<?>, Map<Field, Integer>> viewBindings = new WeakHashMap<>();
}
