package io.freefair.android.injection.modules;

import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;

import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.injection.modules.retrofit.ServiceProvider;
import io.freefair.android.util.function.Consumer;
import io.freefair.android.util.function.Predicate;
import io.freefair.android.util.function.Supplier;
import io.freefair.android.util.function.Suppliers;
import retrofit.Retrofit;

/**
 * An {@link InjectionModule} which enables the injection of {@link Retrofit the retrofit instance}
 * and Services.
 */
@SuppressWarnings("unused")
public class RetrofitModule implements InjectionModule {

    private Consumer<Retrofit.Builder> configurator;
    private Predicate<Class<?>> servicePredicate;

    /**
     * Create a new RetrofitModule
     *
     * @param configurator     Use this, to configure your retrofit instance (baseUrl etc.)
     *                         The {@link OkHttpClient client} is already set
     * @param servicePredicate This one will be used in order to identify services.
     *                         Return true here, if the given interface is a service.
     */
    public RetrofitModule(Consumer<Retrofit.Builder> configurator, Predicate<Class<?>> servicePredicate) {
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
                OkHttpClient client = injectionContainer.resolveValue(OkHttpClient.class, null);
                if (client != null) {
                    builder.client(client);
                }
                configurator.accept(builder);
                return builder.build();
            }
        }));

        injectionContainer.registerProvider(new ServiceProvider(servicePredicate));
    }
}
