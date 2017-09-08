package io.freefair.android.injection.injector;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * @author Lars Grefer
 */
public class ActivityInjector extends AndroidViewInjector<Activity> {

    public ActivityInjector(Activity activity, Object... possibleParents) {
        super(activity, possibleParents);
    }

    @Override
    protected View findViewById(@IdRes int id) {
        return getObject().findViewById(id);
    }

    @Override
    protected Context getNearestContext(Object instance) {
        return getObject();
    }
}
