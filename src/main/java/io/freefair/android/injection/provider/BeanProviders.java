package io.freefair.android.injection.provider;

import java.util.Arrays;
import java.util.Collection;

import io.freefair.android.injection.injector.Injector;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BeanProviders {

    /**
     * Create a {@link BeanProvider} providing the given Object
     *
     * @param object the Object to provide
     * @param <B> the type of the Object to provide
     * @return A {@link BeanProvider} providing the given Object
     */
    @SuppressWarnings("unchecked")
    public static <B> BeanProvider of(@NonNull B object) {
        return of((Class<? super B>) object.getClass(), object);
    }

    public static <B> BeanProvider of(@NonNull Class<? super B> type, B object) {
        return ofSupplier(type, Suppliers.of(object));
    }

    public static <B> BeanProvider ofSupplier(@NonNull Class<B> type, @NonNull Supplier<? extends B> supplier) {
        return new SupplierProvider<>(type, supplier);
    }

    public static <IMPL extends IFACE, IFACE> BeanProvider registerType(Class<IFACE> interfaceClass, Class<IMPL> implClass) {
        return new TypeRegistration<>(interfaceClass, implClass);
    }

    public static BeanProvider combine(BeanProvider... beanProviders) {
        return combine(Arrays.asList(beanProviders));
    }

    public static BeanProvider combine(@NonNull Collection<? extends BeanProvider> beanProviders) {
        return new CombiningBeanProvider(beanProviders);
    }

    @RequiredArgsConstructor
    private static class CombiningBeanProvider implements BeanProvider {

        private final Collection<? extends BeanProvider> beanProviders;

        private BeanProvider lastProvider;

        @Override
        public boolean canProvideBean(Class<?> type) {
            for (BeanProvider beanProvider : beanProviders) {
                if (beanProvider.canProvideBean(type)) {
                    lastProvider = beanProvider;
                    return true;
                }
            }
            return false;
        }

        @Override
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {

            if (lastProvider != null && lastProvider.canProvideBean(clazz))
                return lastProvider.provideBean(clazz, instance, injector);

            for (BeanProvider beanProvider : beanProviders) {
                if (beanProvider.canProvideBean(clazz))
                    return beanProvider.provideBean(clazz, instance, injector);
            }

            return null;
        }
    }

    @RequiredArgsConstructor
    private static class SupplierProvider<T> implements BeanProvider {

        private final Class<T> type;
        private final Supplier<? extends T> supplier;

        @Override
        public boolean canProvideBean(Class<?> type) {
            return type.isAssignableFrom(this.type);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <B> B provideBean(Class<? super B> clazz, Object instance, Injector injector) {
            T value = supplier.get();
            if (value == null) return null;
            injector.inject(value);
            return (B) value;
        }
    }

    @RequiredArgsConstructor
    private static class TypeRegistration<IMPL extends IFACE, IFACE> implements BeanProvider {

        private final Class<IFACE> interfaceClass;
        private final Class<IMPL> implementationClass;

        @Override
        public boolean canProvideBean(Class<?> type) {
            return type.isAssignableFrom(interfaceClass);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
            return (T) injector.resolveBean(implementationClass, instance).orNull();
        }
    }
}
