package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@SuppressWarnings("unused")
public class OkHttp3Module implements InjectionModule {

    @NonNull
    private Consumer<OkHttpClient.Builder> configurator;

    /**
     * Create a new {@link OkHttp3Module} with the given configurator
     *
     * @param configurator A {@link Consumer} which will get the {@link OkHttpClient.Builder}
     *                     in order to perform further configuration
     */
    public OkHttp3Module(@NonNull Consumer<OkHttpClient.Builder> configurator) {
        this.configurator = configurator;
    }

    @Override
    public void configure(RuntimeInjector runtimeInjector) {
        runtimeInjector.registerSupplier(OkHttpClient.class, Suppliers.cache(new Supplier<OkHttpClient>() {
            @Nullable
            @Override
            public OkHttpClient get() {
                OkHttpClient.Builder client = new OkHttpClient.Builder();
                configurator.accept(client);
                return client.build();
            }
        }));
    }

    /**
     * @return An {@link OkHttp3Module} with an empty (default) configuration
     */
    public static OkHttp3Module withEmptyConfig() {
        return new OkHttp3Module(new Consumer<OkHttpClient.Builder>() {
            @Override
            public void accept(OkHttpClient.Builder okHttpClientBuilder) {
            }
        });
    }

    /**
     * Create an {@link OkHttp3Module} with an 10MB cache
     *
     * @param context Context of the current Application.
     *                Used for {@link Context#getCacheDir()}
     * @return An {@link OkHttp3Module} with an 10MB cache
     */
    public static OkHttp3Module withCache(@NonNull final Context context) {
        return new OkHttp3Module(new Consumer<OkHttpClient.Builder>() {
            @Override
            public void accept(OkHttpClient.Builder okHttpClientBuilder) {
                File cacheDir = new File(context.getCacheDir(), "okhttp3");
                long size = 1024 * 1024 * 10;
                okHttpClientBuilder.cache(new Cache(cacheDir, size));
            }
        });
    }
}
