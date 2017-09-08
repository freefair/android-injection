package io.freefair.android.injection.injector;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @author Lars Grefer
 */
public class FragmentInjector extends AndroidViewInjector<Fragment> {

    public FragmentInjector(Fragment fragment, Object... possibleParents) {
        super(fragment, possibleParents);
    }

    @Override
    protected View findViewById(@IdRes int viewId) {

        if (getObject().getView() != null) {
            return getObject().getView().findViewById(viewId);
        }
        return null;
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject().getActivity();
    }
}
