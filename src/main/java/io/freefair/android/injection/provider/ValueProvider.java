package io.freefair.android.injection.provider;

public interface ValueProvider {
    boolean canProvideValue(String key, Class<?> type);

    <V> V provideValue(String key, Class<? super V> type);
}