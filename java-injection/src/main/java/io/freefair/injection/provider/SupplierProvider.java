package io.freefair.injection.provider;

import io.freefair.injection.injector.Injector;
import io.freefair.util.function.Supplier;

/**
 * Created by larsgrefer on 17.09.16.
 */
public class SupplierProvider<T> implements BeanProvider {

    private final Class<T> type;
    private final Supplier<? extends T> supplier;

    public SupplierProvider(Class<T> type, Supplier<? extends T> supplier) {
        this.type = type;
        this.supplier = supplier;
    }

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
