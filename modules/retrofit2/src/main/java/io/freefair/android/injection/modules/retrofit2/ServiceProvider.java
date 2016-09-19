package io.freefair.android.injection.modules.retrofit2;

import android.support.annotation.NonNull;

import io.freefair.injection.provider.BeanProvider;
import io.freefair.injection.injector.Injector;
import io.freefair.util.function.Predicate;
import retrofit2.Retrofit;

@SuppressWarnings("unused")
public class ServiceProvider implements BeanProvider {

    @NonNull
    private Predicate<Class<?>> servicePredicate;

    public ServiceProvider(@NonNull Predicate<Class<?>> servicePredicate) {
        this.servicePredicate = servicePredicate;
    }

    @Override
    public boolean canProvideBean(Class<?> type) {
        return type.isInterface() && servicePredicate.test(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
        Retrofit restAdapter = injector.resolveBean(Retrofit.class, instance);
        return (T) restAdapter.create(clazz);
    }
}
