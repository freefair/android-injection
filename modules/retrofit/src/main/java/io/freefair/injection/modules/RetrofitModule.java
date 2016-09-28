package io.freefair.injection.modules;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.InjectionModuleBase;
import io.freefair.injection.injector.Injector;
import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.provider.BeanProviders;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Predicate;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import retrofit.RestAdapter;

/**
 * An {@link InjectionModule} which enables the injection of {@link RestAdapter the RestAdapter instance}
 * and Services.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RetrofitModule extends InjectionModuleBase {

    @NonNull
    private final Supplier<RestAdapter> restAdapterSupplier;

    @NonNull
    private final Predicate<Class<?>> servicePredicate;

    @Override
    public Optional<? extends BeanProvider> getBeanProvider() {
        BeanProvider retrofitProvider = BeanProviders.ofSupplier(RestAdapter.class, restAdapterSupplier);

        ServiceProvider serviceProvider = new ServiceProvider(servicePredicate);

        return Optional.of(BeanProviders.combine(retrofitProvider, serviceProvider));
    }

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor
    private class ServiceProvider implements BeanProvider {

        @NonNull
        private final Predicate<Class<?>> servicePredicate;

        @Override
        public boolean canProvideBean(Class<?> type) {
            return type.isInterface() && servicePredicate.test(type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
            return (T) restAdapterSupplier.get().create(clazz);
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {

        @Nullable
        private Supplier<RestAdapter> restAdapterSupplier;

        @Nullable
        private Predicate<Class<?>> servicePredicate;

        public Builder restAdapter(@NonNull Supplier<RestAdapter> restAdapterSupplier) {
            this.restAdapterSupplier = restAdapterSupplier;
            return this;
        }

        public Builder restAdapter(@NonNull RestAdapter retrofit) {
            restAdapterSupplier = Suppliers.of(retrofit);
            return this;
        }

        public Builder restAdapter(@NonNull Consumer<RestAdapter.Builder> configurator) {
            return restAdapter(new RestAdapterSupplier(configurator));
        }

        public Builder services(@NonNull Predicate<Class<?>> servicePredicate) {
            this.servicePredicate = servicePredicate;
            return this;
        }

        public Builder services(@NonNull Collection<Class<?>> services) {
            this.servicePredicate = new ServiceCollectionPredicate(services);
            return this;
        }

        public Builder services(Class<?>... services) {
            return services(Arrays.asList(services));
        }

        public RetrofitModule build() {

            if (restAdapterSupplier == null)
                throw new IllegalStateException("restAdapter not set");

            if (servicePredicate == null)
                throw new IllegalStateException("services not set");

            return new RetrofitModule(Suppliers.cache(restAdapterSupplier), servicePredicate);
        }

        @RequiredArgsConstructor
        private static class RestAdapterSupplier implements Supplier<RestAdapter> {

            @NonNull
            private final Consumer<RestAdapter.Builder> configurator;

            @Override
            public RestAdapter get() {
                RestAdapter.Builder builder = new RestAdapter.Builder();
                configurator.accept(builder);
                return builder.build();
            }
        }

        @RequiredArgsConstructor
        private static class ServiceCollectionPredicate implements Predicate<Class<?>> {

            @NonNull
            private final Collection<Class<?>> list;

            @Override
            public boolean test(@Nullable Class<?> t) {
                return list.contains(t);
            }
        }
    }
}
