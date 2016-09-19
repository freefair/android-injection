package io.freefair.android.injection.injector;

import android.app.Application;
import android.os.Bundle;

import io.freefair.util.function.Optional;

public class ApplicationInjector extends AndroidResourceInjector<Application> {

    public ApplicationInjector(Application object, Object... possibleParents) {
        super(object, possibleParents);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> resolveValue(String key, Class<V> type) {

        Bundle metaData = getObject().getApplicationInfo().metaData;

        if (metaData.containsKey(key)) {
            Object value = metaData.get(key);
            if (type.isInstance(value)) {
                return Optional.of((V) value);
            }
        }

        return super.resolveValue(key, type);
    }
}
