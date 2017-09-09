package io.freefair.android.injection.annotation;

import android.support.annotation.AttrRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Lars Grefer
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface InjectAttribute {

    /**
     * The {@link android.R.attr R.attr}-ID of the Attribute to inject.
     */
    @AttrRes int id();

    /**
     * The type of attribute to inject.
     */
    AttributeType type();
}
