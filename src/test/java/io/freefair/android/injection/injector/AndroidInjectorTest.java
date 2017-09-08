package io.freefair.android.injection.injector;

import android.app.Activity;
import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Lars Grefer
 */
public class AndroidInjectorTest {

    @Test
    public void typeTest() {
        assertTrue(Context.class.isAssignableFrom(Activity.class));
    }

}
