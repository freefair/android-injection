package io.freefair.android.injection.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.freefair.android.injection.annotation.XmlLayout;
import io.freefair.android.injection.annotation.XmlMenu;
import io.freefair.android.injection.injector.FragmentInjector;
import io.freefair.android.injection.provider.InjectorProvider;
import io.freefair.android.injection.annotation.Inject;
import io.freefair.util.function.Optional;

/**
 * @author Lars Grefer
 */
@SuppressWarnings("unused")
public abstract class InjectionFragment extends Fragment implements InjectorProvider {

    private FragmentInjector fragmentInjector;

    @Inject
    private Optional<XmlMenu> xmlMenuAnnotation;
    @Inject
    private Optional<XmlLayout> xmlLayoutAnnotation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fragmentInjector = new FragmentInjector(this, getActivity(), getActivity().getApplication());
        super.onCreate(savedInstanceState);

        fragmentInjector.inject(this);
        if (xmlMenuAnnotation.isPresent()) {
            setHasOptionsMenu(true);
        }

        injectAttributesAndResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (xmlLayoutAnnotation.isPresent()) {
            return inflater.inflate(xmlLayoutAnnotation.get().value(), container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentInjector.injectViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (xmlMenuAnnotation.isPresent()) {
            inflater.inflate(xmlMenuAnnotation.get().value(), menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        injectAttributesAndResources();
    }

    private void injectAttributesAndResources() {
        if (fragmentInjector != null) {
            fragmentInjector.injectResources();
            fragmentInjector.injectAttributes();
        }
    }

    @Override
    public FragmentInjector getInjector() {
        return fragmentInjector;
    }
}
