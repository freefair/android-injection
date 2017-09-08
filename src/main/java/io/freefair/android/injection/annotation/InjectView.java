package io.freefair.android.injection.annotation;

import android.support.annotation.IdRes;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Lars Grefer
 */
@Target(FIELD)
@Retention(RUNTIME)
@Inherited
public @interface InjectView {
    @IdRes int value();
}
