package io.freefair.android.injection.annotation;

import android.support.annotation.AnyRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Lars Grefer
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface InjectResource {

    /**
     * The {@link android.R R.*}-ID of the resource to inject.
     */
    @AnyRes int id();

    /**
     * The type of resource to inject.
     */
    ResourceType type();
}
