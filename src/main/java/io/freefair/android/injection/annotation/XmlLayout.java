package io.freefair.android.injection.annotation;

import android.support.annotation.LayoutRes;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Lars Grefer
 */
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface XmlLayout {

    /**
     * The {@link android.R.layout R.layout}-ID of the layout to use for the annotated
     * {@link io.freefair.android.injection.app.InjectionAppCompatActivity Activity} or
     * {@link io.freefair.android.injection.app.InjectionFragment Fragment}.
     */
    @LayoutRes int value();
}
