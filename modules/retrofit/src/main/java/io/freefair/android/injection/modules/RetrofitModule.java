package io.freefair.android.injection.modules;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.modules.retrofit.ServiceProvider;
import io.freefair.android.util.function.Consumer;
import io.freefair.android.util.function.Predicate;
import io.freefair.android.util.function.Supplier;
import io.freefair.android.util.function.Suppliers;
import retrofit.RestAdapter;

/**
 * An {@link InjectionModule} which enables the injection of {@link RestAdapter the RestAdapter instance}
 * and Services.
 */
@SuppressWarnings("unused")
public class RetrofitModule implements InjectionModule {

    @NonNull
    private Consumer<RestAdapter.Builder> configurator;
    @NonNull
    private Predicate<Class<?>> servicePredicate;

    /**
     * Create a new {@link RetrofitModule}
     *
     * @param configurator     Use this, to configure your {@link RestAdapter} instance (baseUrl etc.)
     * @param servicePredicate This one will be used in order to identify services.
     *                         Return true here, if the given interface is a service.
     */
    public RetrofitModule(@NonNull Consumer<RestAdapter.Builder> configurator, @NonNull Predicate<Class<?>> servicePredicate) {
        this.configurator = configurator;
        this.servicePredicate = servicePredicate;
    }

    @Override
    public void configure(final InjectionContainer injectionContainer) {
        injectionContainer.registerSupplier(RestAdapter.class, Suppliers.cache(new Supplier<RestAdapter>() {
            @Nullable
            @Override
            public RestAdapter get() {
                RestAdapter.Builder builder = new RestAdapter.Builder();
                configurator.accept(builder);
                return builder.build();
            }
        }));

        injectionContainer.registerProvider(new ServiceProvider(servicePredicate));
    }
}
