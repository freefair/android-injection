package io.freefair.android.injection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.freefair.android.injection.annotation.Inject;
import io.freefair.android.injection.injector.RuntimeInjector;
import io.freefair.android.injection.provider.BeanProviders;

public class InjectorTest {

    RuntimeInjector injector;

    @Before
    public void setUp() {
        injector = RuntimeInjector.getInstance();
        injector.register(BeanProviders.of(String.class, "FOO"));
    }

    @Test
    public void testInjection() {

        A a = new A();

        injector.inject(a);

        Assert.assertEquals("FOO", a.getB().getTest());
    }

    public static class A {

        @Inject
        private B b;

        public B getB() {
            return b;
        }
    }

    public static class B {

        @Inject
        private String test;

        public String getTest() {
            return test;
        }
    }

    @Test
    public void testCircleInjection() {
        Circle circle = new Circle();

        injector.inject(circle);
    }

    public static class Circle {
        @Inject
        Circle1 c;
    }

    public static class Circle1 {
        @Inject
        Circle2 c2;
    }

    public static class Circle2 {
        @Inject
        Circle1 c1;
    }

}