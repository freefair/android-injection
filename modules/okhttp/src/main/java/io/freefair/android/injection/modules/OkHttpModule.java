package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.InjectionModuleBase;
import io.freefair.injection.provider.BeanProviders;
import io.freefair.util.function.Consumer;
import io.freefair.util.function.Optional;
import io.freefair.util.function.Supplier;
import io.freefair.util.function.Suppliers;

@SuppressWarnings("unused")
public class OkHttpModule extends InjectionModuleBase {

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
    public Optional<? extends BeanProvider> getBeanProvider() {
        BeanProvider okHttpClientSupplierProvider = BeanProviders.ofSupplier(OkHttpClient.class, Suppliers.cache(new Supplier<OkHttpClient>() {
            @Nullable
            @Override
            public OkHttpClient get() {
                OkHttpClient client = new OkHttpClient();
                configurator.accept(client);
                return client;
            }
        }));

        return Optional.of(okHttpClientSupplierProvider);
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
