package io.freefair.android.injection.modules;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.InjectionModuleBase;
import io.freefair.injection.injector.Injector;
import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.provider.CombiningBeanProvider;
import io.freefair.injection.provider.SupplierProvider;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Predicate;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;
import retrofit.RestAdapter;

/**
 * An {@link InjectionModule} which enables the injection of {@link RestAdapter the RestAdapter instance}
 * and Services.
 */
@SuppressWarnings("unused")
public class RetrofitModule extends InjectionModuleBase {

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
    public Optional<? extends BeanProvider> getBeanProvider() {
        SupplierProvider<RestAdapter> restAdapterProvider = new SupplierProvider<>(RestAdapter.class, Suppliers.cache(new Supplier<RestAdapter>() {
            @Nullable
            @Override
            public RestAdapter get() {
                RestAdapter.Builder builder = new RestAdapter.Builder();
                configurator.accept(builder);
                return builder.build();
            }
        }));

        ServiceProvider serviceProvider = new ServiceProvider(servicePredicate);

        return Optional.of(new CombiningBeanProvider(restAdapterProvider, serviceProvider));
    }

    public static class ServiceProvider implements BeanProvider {

        @NonNull
        private Predicate<Class<?>> servicePredicate;

        public ServiceProvider(@NonNull Predicate<Class<?>> servicePredicate) {
            this.servicePredicate = servicePredicate;
        }

        @Override
        public boolean canProvideBean(Class<?> type) {
            return type.isInterface() && servicePredicate.test(type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
            Optional<? extends RestAdapter> restAdapter = injector.resolveBean(RestAdapter.class, instance);
            if (restAdapter.isPresent()) {
                return (T) restAdapter.get().create(clazz);
            } else {
                return null;
            }
        }
    }
}
