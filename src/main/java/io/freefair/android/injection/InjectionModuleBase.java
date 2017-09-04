package io.freefair.android.injection;

import io.freefair.android.injection.provider.BeanProvider;
import io.freefair.android.injection.provider.ValueProvider;
import io.freefair.util.function.Optional;

/**
 * Created by larsgrefer on 17.09.16.
 */
public abstract class InjectionModuleBase implements InjectionModule {


    @Override
    public Optional<? extends BeanProvider> getBeanProvider() {
        return Optional.empty();
    }

    @Override
    public Optional<? extends ValueProvider> getValueProvider() {
        return Optional.empty();
    }
}
