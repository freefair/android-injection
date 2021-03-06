package io.freefair.android.injection.injector;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.view.View;

import io.freefair.util.function.Optional;

/**
 * @author Dennis Fricke
 */
public class WatchViewStubActivityInjector extends AndroidViewInjector<Activity> {

    private final WatchViewStub stub;

    public WatchViewStubActivityInjector(Activity activity, WatchViewStub stub, Object... possibleParents) {
        super(activity, possibleParents);
        this.stub = stub;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<? extends T> resolveBean(@NonNull Class<T> type, Object instance) {
        if (type.isAssignableFrom(Context.class)) {
            return Optional.of((T) stub.getContext());
        }

        return super.resolveBean(type, instance);
    }

    @Override
    protected View findViewById(@IdRes int id) {
        return stub.findViewById(id);
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject();
    }
}
