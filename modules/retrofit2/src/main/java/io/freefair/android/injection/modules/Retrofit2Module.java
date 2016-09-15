package io.freefair.android.injection.modules;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.android.injection.modules.retrofit2.ServiceProvider;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Predicate;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;
import retrofit2.Retrofit;

/**
 * An {@link InjectionModule} which enables the injection of {@link Retrofit the retrofit instance}
 * and Services.
 */
@SuppressWarnings("unused")
public class Retrofit2Module implements InjectionModule {

    @NonNull
    private Consumer<Retrofit.Builder> configurator;
    @NonNull
    private Predicate<Class<?>> servicePredicate;

    /**
     * Create a new {@link Retrofit2Module}
     *
     * @param configurator     Use this, to configure your {@link Retrofit} instance (baseUrl etc.)
     * @param servicePredicate This one will be used in order to identify services.
     *                         Return true here, if the given interface is a service.
     */
    public Retrofit2Module(
            @NonNull Consumer<Retrofit.Builder> configurator,
            @NonNull Predicate<Class<?>> servicePredicate
    ) {
        this.configurator = configurator;
        this.servicePredicate = servicePredicate;
    }

    @Override
    public void configure(final RuntimeInjector runtimeInjector) {
        runtimeInjector.registerSupplier(Retrofit.class, Suppliers.cache(new Supplier<Retrofit>() {
            @Nullable
            @Override
            public Retrofit get() {
                Retrofit.Builder builder = new Retrofit.Builder();
                configurator.accept(builder);
                return builder.build();
            }
        }));

        runtimeInjector.registerProvider(new ServiceProvider(servicePredicate));
    }
}
