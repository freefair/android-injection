package io.freefair.android.injection.helper;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import io.freefair.android.injection.annotation.RClass;
import io.freefair.android.util.logging.*;

public class RClassHelper {

	protected static Logger log = AndroidLogger.forClass(RClassHelper.class);

	@Nullable
	static Class<?> fromAnnotation(Object object) {
		if (object.getClass().isAnnotationPresent(RClass.class)) {
			Class<?> rClass = object.getClass().getAnnotation(RClass.class).value();
			String rClassName = rClass.getSimpleName();
			if (!rClassName.equals("R")) {
				log.warn("The name of the class given via @RClass should be 'R', but was '" + rClassName + "'");
			}
			return rClass;
		}
		return null;
	}

	@Nullable
	public static Class<?> fromFragment(Fragment fragment) {
		Class<?> rClass = fromAnnotation(fragment);
		if (rClass != null) {
			return rClass;
		}
		return fromActivity(fragment.getActivity());
	}

	@Nullable
	public static Class<?> fromActivity(Activity activity) {
		Class<?> rClass = fromAnnotation(activity);
		if (rClass != null) {
			return rClass;
		}
		return fromApplication(activity.getApplication());
	}

	@Nullable
	public static Class<?> fromService(Service service){
		Class<?> rClassFromAnnotation = fromAnnotation(service);
		if(rClassFromAnnotation != null)
			return rClassFromAnnotation;
		return fromApplication(service.getApplication());
	}

	@Nullable
	private static Class<?> fromApplication(Application application) {
		Class<?> rClass = fromAnnotation(application);
		if (rClass != null) {
			return rClass;
		}
		return fromPackageName(application.getPackageName());
	}

	@Nullable
	static Class<?> fromPackageName(String packageName) {
		String rClassName = packageName + ".R";
		try {
			return Class.forName(rClassName);
		} catch (ClassNotFoundException e) {
			log.info("No R class found for given package name " + packageName, e);
		}
		return null;
	}

	@Nullable
	public static Class<?> fromViewGroup(ViewGroup object) {
		Class<?> rClass = fromAnnotation(object);
		if (rClass != null) {
			return rClass;
		}
		return fromActivity((Activity) object.getContext());
	}
}
