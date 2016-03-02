package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

public class FragmentInjector extends AndroidViewInjector<Fragment> {

    public FragmentInjector(Fragment fragment, Injector parentInjector) {
        super(parentInjector, fragment);
    }

    @Override
    protected View findViewById(@IdRes int viewId) {

        if (getObject().getView() != null) {
            return getObject().getView().findViewById(viewId);
        }
        return null;
    }
}
