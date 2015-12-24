package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import io.freefair.android.injection.injector.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.util.function.Consumer;
import io.freefair.android.util.function.Supplier;
import io.freefair.android.util.function.Suppliers;

@SuppressWarnings("unused")
public class OkHttpModule implements InjectionModule {

    private Consumer<OkHttpClient> configurator;

    public OkHttpModule(Consumer<OkHttpClient> configurator){
        this.configurator = configurator;
    }

    @Override
    public void configure(InjectionContainer injectionContainer) {
        injectionContainer.registerSupplier(OkHttpClient.class, Suppliers.cache(new Supplier<OkHttpClient>() {
            @Nullable
            @Override
            public OkHttpClient get() {
                OkHttpClient client = new OkHttpClient();
                configurator.accept(client);
                return client;
            }
        }));
    }

    public static OkHttpModule withEmptyConfig(){
        return new OkHttpModule(new Consumer<OkHttpClient>() {
            @Override
            public void accept(OkHttpClient value) {

            }
        });
    }

    public static OkHttpModule withCache(final Context context){
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
