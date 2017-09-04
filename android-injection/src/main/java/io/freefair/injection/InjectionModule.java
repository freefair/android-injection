package io.freefair.injection;

import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.provider.ValueProvider;
import io.freefair.util.function.Optional;

public interface InjectionModule {

    Optional<? extends BeanProvider> getBeanProvider();

    Optional<? extends ValueProvider> getValueProvider();
}
