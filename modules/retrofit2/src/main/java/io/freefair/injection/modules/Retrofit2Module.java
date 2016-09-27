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
import retrofit2.Retrofit;

/**
 * An {@link InjectionModule} which enables the injection of {@link Retrofit the retrofit instance}
 * and Services.
 */
@RequiredArgsConstructor
@SuppressWarnings({"unused", "WeakerAccess"})
public class Retrofit2Module extends InjectionModuleBase {

    @NonNull
    private final Supplier<Retrofit> retrofitSupplier;

    @NonNull
    private final Predicate<Class<?>> servicePredicate;

    @Override
    public Optional<? extends BeanProvider> getBeanProvider() {
        BeanProvider retrofitProvider = BeanProviders.ofSupplier(Retrofit.class, retrofitSupplier);

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
            return (T) retrofitSupplier.get().create(clazz);
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {

        @Nullable
        private Supplier<Retrofit> retrofitSupplier;

        @Nullable
        private Predicate<Class<?>> servicePredicate;

        public Builder retrofit(@NonNull Supplier<Retrofit> retrofitSupplier) {
            this.retrofitSupplier = Suppliers.cache(retrofitSupplier);
            return this;
        }

        public Builder retrofit(@NonNull Retrofit retrofit) {
            retrofitSupplier = Suppliers.of(retrofit);
            return this;
        }

        public Builder retrofit(@NonNull Consumer<Retrofit.Builder> configurator) {
            return retrofit(new RetrofitSupplier(configurator));
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

        public Retrofit2Module build() {

            if (retrofitSupplier == null)
                throw new IllegalStateException("retrofit not set");

            if (servicePredicate == null)
                throw new IllegalStateException("services not set");

            return new Retrofit2Module(retrofitSupplier, servicePredicate);
        }

        @RequiredArgsConstructor
        private static class RetrofitSupplier implements Supplier<Retrofit> {

            @NonNull
            private final Consumer<Retrofit.Builder> configurator;

            @Override
            public Retrofit get() {
                Retrofit.Builder builder = new Retrofit.Builder();
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
