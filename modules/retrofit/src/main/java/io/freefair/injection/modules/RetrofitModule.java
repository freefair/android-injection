package io.freefair.injection.modules;

import java.util.Arrays;
import java.util.Collection;

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
import lombok.RequiredArgsConstructor;
import retrofit.RestAdapter;

/**
 * An {@link InjectionModule} which enables the injection of {@link RestAdapter the RestAdapter instance}
 * and Services.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RetrofitModule extends InjectionModuleBase {

    @lombok.NonNull
    private final Supplier<RestAdapter> restAdapterSupplier;

    @lombok.NonNull
    private final Predicate<Class<?>> servicePredicate;

    @Override
    public Optional<? extends BeanProvider> getBeanProvider() {
        SupplierProvider<RestAdapter> retrofitProvider = new SupplierProvider<>(RestAdapter.class, restAdapterSupplier);

        ServiceProvider serviceProvider = new ServiceProvider(servicePredicate);

        return Optional.of(new CombiningBeanProvider(retrofitProvider, serviceProvider));
    }

    public static Builder builder() {
        return new Builder();
    }

    @RequiredArgsConstructor
    private class ServiceProvider implements BeanProvider {

        @lombok.NonNull
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

        @org.jetbrains.annotations.Nullable
        private Supplier<RestAdapter> restAdapterSupplier;

        @org.jetbrains.annotations.Nullable
        private Predicate<Class<?>> servicePredicate;

        public Builder restAdapter(@lombok.NonNull Supplier<RestAdapter> restAdapterSupplier) {
            this.restAdapterSupplier = Suppliers.cache(restAdapterSupplier);
            return this;
        }

        public Builder restAdapter(@lombok.NonNull RestAdapter retrofit) {
            restAdapterSupplier = Suppliers.of(retrofit);
            return this;
        }

        public Builder restAdapter(@lombok.NonNull Consumer<RestAdapter.Builder> configurator) {
            return restAdapter(new RestAdapterSupplier(configurator));
        }

        public Builder services(@lombok.NonNull Predicate<Class<?>> servicePredicate) {
            this.servicePredicate = servicePredicate;
            return this;
        }

        public Builder services(@lombok.NonNull Collection<Class<?>> services) {
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

            return new RetrofitModule(restAdapterSupplier, servicePredicate);
        }

        @RequiredArgsConstructor
        private static class RestAdapterSupplier implements Supplier<RestAdapter> {

            @lombok.NonNull
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

            @lombok.NonNull
            private final Collection<Class<?>> list;

            @Override
            public boolean test(@org.jetbrains.annotations.Nullable Class<?> t) {
                return list.contains(t);
            }
        }
    }
}
