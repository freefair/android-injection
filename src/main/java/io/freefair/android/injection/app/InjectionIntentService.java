package io.freefair.android.injection.app;

import android.app.IntentService;
import android.content.res.Configuration;

import io.freefair.android.injection.injector.IntentServiceInjector;
import io.freefair.android.injection.provider.InjectorProvider;

/**
 * @author Lars Grefer
 */
public abstract class InjectionIntentService extends IntentService implements InjectorProvider {

    private IntentServiceInjector intentServiceInjector;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public InjectionIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        intentServiceInjector = new IntentServiceInjector(this, getApplication());
        super.onCreate();
        intentServiceInjector.inject(this);
        intentServiceInjector.injectAttributes();
        intentServiceInjector.injectResources();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (intentServiceInjector != null) {
            intentServiceInjector.injectAttributes();
            intentServiceInjector.injectResources();
        }
    }

    @Override
    public IntentServiceInjector getInjector() {
        return intentServiceInjector;
    }
}
