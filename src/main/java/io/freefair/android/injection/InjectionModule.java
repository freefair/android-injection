package io.freefair.android.injection;

import io.freefair.android.injection.provider.BeanProvider;
import io.freefair.android.injection.provider.ValueProvider;
import io.freefair.util.function.Optional;

public interface InjectionModule {

    Optional<? extends BeanProvider> getBeanProvider();

    Optional<? extends ValueProvider> getValueProvider();
}
