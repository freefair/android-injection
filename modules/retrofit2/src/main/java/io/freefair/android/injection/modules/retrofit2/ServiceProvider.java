package io.freefair.android.injection.modules.retrofit2;

import io.freefair.android.injection.InjectionProvider;
import io.freefair.android.injection.injector.Injector;
import io.freefair.android.util.function.Predicate;
import retrofit2.Retrofit;

@SuppressWarnings("unused")
public class ServiceProvider implements InjectionProvider {

    private Predicate<Class<?>> servicePredicate;

    public ServiceProvider(Predicate<Class<?>> servicePredicate) {
        this.servicePredicate = servicePredicate;
    }

    @Override
    public boolean canProvide(Class<?> clazz) {
        return clazz.isInterface() && servicePredicate.test(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<? super T> clazz, Object instance, Injector injector) {
        Retrofit restAdapter = injector.resolveValue(Retrofit.class, instance);
        return (T) restAdapter.create(clazz);
    }
}
