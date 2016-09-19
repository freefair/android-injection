package io.freefair.android.injection.modules;

import io.freefair.android.util.logging.AndroidLogger;
import io.freefair.injection.InjectionModuleBase;
import io.freefair.injection.injector.Injector;
import io.freefair.injection.provider.BeanProvider;
import io.freefair.util.function.Optional;

@SuppressWarnings("unused")
public class AndroidLoggerModule extends InjectionModuleBase {

    @Override
    public Optional<AndroidLoggerProvider> getBeanProvider() {
        return Optional.of(new AndroidLoggerProvider());
    }

    public static class AndroidLoggerProvider implements BeanProvider {

        @Override
        public boolean canProvideBean(Class<?> type) {
            return type.isAssignableFrom(AndroidLogger.class);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {
            return (T) AndroidLogger.forObject(instance);
        }
    }
}
