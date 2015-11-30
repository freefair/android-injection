package io.freefair.android.injection.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import io.freefair.android.injection.InjectionContainer;
import io.freefair.android.injection.InjectionModule;
import io.freefair.android.util.function.CachingSupplier;
import io.freefair.android.util.function.Supplier;
import io.freefair.android.util.function.Suppliers;

@SuppressWarnings("unused")
public class OkHttpModule implements InjectionModule {

    private Supplier<Cache> cacheSupplier;
    private Supplier<OkHttpClient> clientSupplier;

    private Context context;

    public OkHttpModule(Context context){
        this.context = context;
    }

    @Override
    public void configure(InjectionContainer injectionContainer) {
        ensureValues();

        injectionContainer.registerSupplier(Cache.class, Suppliers.cache(cacheSupplier));
    }

    private void ensureValues() {
        if(cacheSupplier == null)
            cacheSupplier = new Supplier<Cache>() {
                @Nullable
                @Override
                public Cache get() {
                    return new Cache(new File(context.getCacheDir(), "okhttp" ), 20 * 1024 * 1024);
                }
            };

        if(clientSupplier == null) {
            clientSupplier = new Supplier<OkHttpClient>() {
                @Nullable
                @Override
                public OkHttpClient get() {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    return okHttpClient;
                }
            };
        }
    }
}
