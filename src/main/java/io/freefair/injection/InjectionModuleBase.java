package io.freefair.injection;

import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.provider.ValueProvider;
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
