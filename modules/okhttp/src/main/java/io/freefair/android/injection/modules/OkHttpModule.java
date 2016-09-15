package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import io.freefair.injection.InjectionModule;
import io.freefair.injection.injector.RuntimeInjector;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;

@SuppressWarnings("unused")
public class OkHttpModule implements InjectionModule {

    @NonNull
    private Consumer<OkHttpClient> configurator;

    /**
     * Create a new {@link OkHttpModule} with the given configurator
     *
     * @param configurator A {@link Consumer} which will get the created {@link OkHttpClient}
     *                     in order to do further configuration
     */
    public OkHttpModule(@NonNull Consumer<OkHttpClient> configurator) {
        this.configurator = configurator;
    }

    @Override
    public void configure(RuntimeInjector runtimeInjector) {
        runtimeInjector.registerSupplier(OkHttpClient.class, Suppliers.cache(new Supplier<OkHttpClient>() {
            @Nullable
            @Override
            public OkHttpClient get() {
                OkHttpClient client = new OkHttpClient();
                configurator.accept(client);
                return client;
            }
        }));
    }

    /**
     * @return An {@link OkHttpModule} with an empty (default) configuration
     */
    public static OkHttpModule withEmptyConfig() {
        return new OkHttpModule(new Consumer<OkHttpClient>() {
            @Override
            public void accept(OkHttpClient value) {

            }
        });
    }

    /**
     * Create an {@link OkHttpModule} with an 10MB cache
     *
     * @param context Context of the current Application.
     *                Used for {@link Context#getCacheDir()}
     * @return An {@link OkHttpModule} with an 10MB cache
     */
    public static OkHttpModule withCache(@NonNull final Context context) {
        return new OkHttpModule(new Consumer<OkHttpClient>() {
            @Override
            public void accept(OkHttpClient value) {
                File cacheDir = new File(context.getCacheDir(), "okhttp");
                long size = 1024 * 1024 * 10;
                value.setCache(new Cache(cacheDir, size));
            }
        });
    }
}
