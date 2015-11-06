package io.freefair.android.injection.helper;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import io.freefair.android.injection.annotation.RClass;
import io.freefair.android.util.log.Logger;
import io.freefair.android.util.log.Loggers;

/**
 * Created by larsgrefer on 27.11.14.
 */
public class RClassHelper {

	protected static Logger log = Loggers.forClass(RClassHelper.class);

	static Class<?> getRClassFromAnnotation(Object object) {
		if (object.getClass().isAnnotationPresent(RClass.class)) {
			Class<?> rClass = object.getClass().getAnnotation(RClass.class).value();
			String rClassName = rClass.getSimpleName();
			if (rClassName.equals("R")) {
				return rClass;
			} else {
				log.error("The name of the class given via @RClass should be 'R', but was '" + rClassName + "'");
			}
		}
		return null;
	}

	public static Class<?> getRClassFromFragment(Fragment fragment) {
		Class<?> rClass = getRClassFromAnnotation(fragment);
		if (rClass != null) {
			return rClass;
		}
		return getRClassFromActivity(fragment.getActivity());
	}

	public static Class<?> getRClassFromActivity(Activity activity) {
		Class<?> rClass = getRClassFromAnnotation(activity);
		if (rClass != null) {
			return rClass;
		}
		return getRClassFromApplication(activity.getApplication());
	}

	private static Class<?> getRClassFromApplication(Application application) {
		Class<?> rClass = getRClassFromAnnotation(application);
		if (rClass != null) {
			return rClass;
		}
		return getRClassFromPackageName(application.getPackageName());
	}

	static Class<?> getRClassFromPackageName(String packageName) {
		String rClassName = packageName + ".R";
		try {
			return Class.forName(rClassName);
		} catch (ClassNotFoundException e) {
			log.info("No R class found for given package name " + packageName, e);
		}
		return null;
	}

	public static Class<?> getRClassFromViewGroup(ViewGroup object) {
		Class<?> rClass = getRClassFromAnnotation(object);
		if (rClass != null) {
			return rClass;
		}
		return getRClassFromActivity((Activity) object.getContext());
	}
}
