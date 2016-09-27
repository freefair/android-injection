package io.freefair.injection.modules;

import org.junit.Test;

import io.freefair.util.function.Consumers;
import retrofit2.Retrofit;

/**
 * Created by larsgrefer on 27.09.16.
 */
public class BuilderTest {

    @Test(expected = IllegalStateException.class)
    public void testBothUnset() {
        Retrofit2Module.builder()
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testFirstUnset() {
        Retrofit2Module.builder()
                .retrofit(Consumers.<Retrofit.Builder>nothing())
                .build();
    }
    @Test(expected = IllegalStateException.class)
    public void testSecondUnset() {
        Retrofit2Module.builder()
                .services()
                .build();
    }
}