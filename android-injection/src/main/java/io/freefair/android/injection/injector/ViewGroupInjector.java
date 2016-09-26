package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dennis Fricke
 */
public class ViewGroupInjector extends AndroidViewInjector<ViewGroup> {
    public ViewGroupInjector(ViewGroup object, Object... possibleParents) {
        super(object, possibleParents);
    }

    @Override
    protected View findViewById(@IdRes int viewId) {
        return getObject().findViewById(viewId);
    }
}
