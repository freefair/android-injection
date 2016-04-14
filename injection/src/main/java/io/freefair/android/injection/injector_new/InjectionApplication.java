package io.freefair.android.injection.injector_new;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import lombok.Getter;

@TargetApi(14)
public class InjectionApplication extends Application implements Application.ActivityLifecycleCallbacks {

	@Getter
	private ContainerBuilder builder;

	private Container container;

	public InjectionApplication() {
		builder = new ContainerBuilder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(this);

		container = builder.build();
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		container.startScope(Scope.Activity);
		container.inject(activity);
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		container.destroyScope(Scope.Activity);
	}
}
