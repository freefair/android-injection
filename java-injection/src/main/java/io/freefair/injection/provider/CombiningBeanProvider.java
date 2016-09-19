package io.freefair.injection.provider;

import java.util.Arrays;
import java.util.Collection;

import io.freefair.injection.injector.Injector;

public class CombiningBeanProvider implements BeanProvider {

    public CombiningBeanProvider(BeanProvider... beanProviders) {
        this(Arrays.asList(beanProviders));
    }

    public CombiningBeanProvider(Collection<? extends BeanProvider> beanProviders) {
        this.beanProviders = beanProviders;
    }

    private final Collection<? extends BeanProvider> beanProviders;

    private BeanProvider lastProvider;

    @Override
    public boolean canProvideBean(Class<?> type) {
        for (BeanProvider beanProvider : beanProviders) {
            if (beanProvider.canProvideBean(type)) {
                lastProvider = beanProvider;
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T provideBean(Class<? super T> clazz, Object instance, Injector injector) {

        if (lastProvider != null && lastProvider.canProvideBean(clazz))
            return lastProvider.provideBean(clazz, instance, injector);

        for (BeanProvider beanProvider : beanProviders) {
            if (beanProvider.canProvideBean(clazz))
                return beanProvider.provideBean(clazz, instance, injector);
        }

        return null;
    }
}
