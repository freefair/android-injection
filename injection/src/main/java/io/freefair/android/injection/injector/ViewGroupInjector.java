package io.freefair.android.injection.injector;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import io.freefair.android.injection.helper.RClassHelper;

/**
 * @author Dennis Fricke
 */
public class ViewGroupInjector extends AndroidViewInjector<ViewGroup> {
	public ViewGroupInjector(ViewGroup object, Injector parentInjector) {
		super(parentInjector, object, RClassHelper.fromViewGroup(object));
	}

	@Override
	protected View findViewById(@IdRes int viewId) {
		return getObject().findViewById(viewId);
	}
}
