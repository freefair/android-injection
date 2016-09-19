package io.freefair.injection.injector;

import io.freefair.injection.provider.InjectorProvider;

public class InjectorUtils {

    static Injector getParentInjector(Object... possibleParents) {
        for (Object possibleParent : possibleParents) {
            if (possibleParent instanceof Injector) {
                return (Injector) possibleParent;
            }

            if (possibleParent instanceof InjectorProvider) {
                return ((InjectorProvider) possibleParent).getInjector();
            }
        }
        return RuntimeInjector.getInstance();
    }
}
