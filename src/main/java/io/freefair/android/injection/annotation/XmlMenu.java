package io.freefair.android.injection.annotation;

import android.support.annotation.MenuRes;

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
public @interface XmlMenu {

    /**
     * The {@link android.R.menu R.menu}-ID of the menu to use for the annotated
     * {@link io.freefair.android.injection.app.InjectionAppCompatActivity Activity} or
     * {@link io.freefair.android.injection.app.InjectionFragment Fragment}.
     */
    @MenuRes int value();
}
