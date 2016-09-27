package io.freefair.injection.provider;

import java.util.Properties;

import io.freefair.util.function.Supplier;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ValueProviders {

    public static <V> ValueProvider of(final String key, final V value) {
        return new ValueProvider() {
            @Override
            public boolean canProvideValue(String k, Class<?> type) {
                return k.equals(key) && type.isInstance(value);
            }

            @Override
            public <V> V provideValue(String key, Class<? super V> type) {
                return (V) value;
            }
        };
    }

    public static <V> ValueProvider ofSupplier(
            @NonNull final String key,
            @NonNull final Class<? super V> type,
            @NonNull final Supplier<V> supplier
    ) {
        return new ValueProvider() {
            @Override
            public boolean canProvideValue(String k, Class<?> t) {
                return k.equals(key) && t.isAssignableFrom(type);
            }

            @Override
            public <V> V provideValue(String key, Class<? super V> type) {
                return (V) supplier.get();
            }
        };
    }

    public static ValueProvider of(Properties properties) {
        return new PropertiesValueProvider(properties);
    }

    @RequiredArgsConstructor
    private static class PropertiesValueProvider implements ValueProvider {

        final Properties properties;

        @Override
        public boolean canProvideValue(String key, Class<?> type) {
            return properties.containsKey(key) && type.isInstance(properties.get(key));
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V> V provideValue(String key, Class<? super V> type) {
            return (V) properties.get(key);
        }
    }
}
