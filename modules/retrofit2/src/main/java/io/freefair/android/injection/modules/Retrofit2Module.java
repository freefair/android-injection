package io.freefair.android.injection.modules;

import android.support.annotation.Nullable;

import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.modules.retrofit2.ServiceProvider;
import io.freefair.android.util.function.Consumer;
import io.freefair.android.util.function.Predicate;
import io.freefair.android.util.function.Supplier;
import io.freefair.android.util.function.Suppliers;
import retrofit2.Retrofit;

/**
 * An {@link InjectionModule} which enables the injection of {@link Retrofit the retrofit instance}
 * and Services.
 */
@SuppressWarnings("unused")
public class Retrofit2Module implements InjectionModule {

    private Consumer<Retrofit.Builder> configurator;
    private Predicate<Class<?>> servicePredicate;

    /**
     * Create a new RetrofitModule
     *
     * @param configurator     Use this, to configure your {@link Retrofit} instance (baseUrl etc.)
     * @param servicePredicate This one will be used in order to identify services.
     *                         Return true here, if the given interface is a service.
     */
    public Retrofit2Module(Consumer<Retrofit.Builder> configurator, Predicate<Class<?>> servicePredicate) {
        this.configurator = configurator;
        this.servicePredicate = servicePredicate;
    }

    @Override
    public void configure(final InjectionContainer injectionContainer) {
        injectionContainer.registerSupplier(Retrofit.class, Suppliers.cache(new Supplier<Retrofit>() {
            @Nullable
            @Override
            public Retrofit get() {
                Retrofit.Builder builder = new Retrofit.Builder();
                configurator.accept(builder);
                return builder.build();
            }
        }));

        injectionContainer.registerProvider(new ServiceProvider(servicePredicate));
    }
}
