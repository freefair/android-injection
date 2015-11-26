package io.freefair.android.injection.platform;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import io.freefair.android.injection.Injector;
import io.freefair.android.injection.helper.RClassHelper;

/**
 * @author Dennis Fricke
 */
public class ViewGroupInjector extends AndroidInjector<ViewGroup> {
	public ViewGroupInjector(ViewGroup object, Injector parentInjector) {
		super(parentInjector, object, RClassHelper.getRClassFromViewGroup(object));
	}

	@Override
	protected View findViewById(@IdRes int viewId) {
		return getObject().findViewById(viewId);
	}
}
