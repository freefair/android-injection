package io.freefair.android.injection.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import io.freefair.android.injection.annotation.XmlLayout;
import io.freefair.android.injection.annotation.XmlMenu;
import io.freefair.android.injection.injector.ActivityInjector;
import io.freefair.android.injection.provider.InjectorProvider;
import io.freefair.android.injection.annotation.Inject;
import io.freefair.util.function.Optional;


/**
 * An {@link AppCompatActivity} with support for dependency injection
 */
@SuppressWarnings("unused")
public abstract class InjectionAppCompatActivity extends AppCompatActivity implements InjectorProvider {

    private ActivityInjector activityInjector;

    @Inject
    protected Optional<XmlMenu> xmlMenuAnnotation;
    @Inject
    protected Optional<XmlLayout> xmlLayoutAnnotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityInjector = new ActivityInjector(this, getApplication());
        super.onCreate(savedInstanceState);

        activityInjector.inject(this);

        if (xmlLayoutAnnotation.isPresent()) {
            setContentView(xmlLayoutAnnotation.get().value());
        }

        injectAttributesAndResources();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        activityInjector.injectViews();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        activityInjector.injectViews();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        activityInjector.injectViews();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        injectAttributesAndResources();
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        injectAttributesAndResources();
    }

    private void injectAttributesAndResources() {
        if (activityInjector != null) {
            activityInjector.injectResources();
            activityInjector.injectAttributes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (xmlMenuAnnotation.isPresent()) {
            getMenuInflater().inflate(xmlMenuAnnotation.get().value(), menu);
            super.onCreateOptionsMenu(menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public ActivityInjector getInjector() {
        return activityInjector;
    }
}
