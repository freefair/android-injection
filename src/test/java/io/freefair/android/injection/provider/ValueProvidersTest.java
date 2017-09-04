package io.freefair.android.injection.provider;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.freefair.util.function.Suppliers;

/**
 * Created by larsgrefer on 27.09.16.
 */
public class ValueProvidersTest {
    @Test
    public void ofString() throws Exception {
        ValueProvider of = ValueProviders.of("foo", "bar");

        Assert.assertTrue(of.canProvideValue("foo", String.class));
        Assert.assertTrue(of.canProvideValue("foo", Object.class));

        Assert.assertEquals("bar", of.provideValue("foo", String.class));
        Assert.assertEquals("bar", of.provideValue("foo", Object.class));
    }

    @Test
    public void ofComplex() throws Exception {
        List<?> list = Collections.emptyList();

        ValueProvider of = ValueProviders.of("foo", list);

        Assert.assertTrue(of.canProvideValue("foo", List.class));
        Assert.assertTrue(of.canProvideValue("foo", Collection.class));
        Assert.assertTrue(of.canProvideValue("foo", Object.class));
        Assert.assertFalse(of.canProvideValue("foo", String.class));

        Assert.assertEquals(list, of.provideValue("foo", List.class));
        Assert.assertEquals(list, of.provideValue("foo", Object.class));
    }

    @Test
    public void ofSupplier() throws Exception {
        List<?> list = new ArrayList<>();

        ValueProvider of = ValueProviders.ofSupplier("foo", List.class, Suppliers.of(list));

        Assert.assertFalse(of.canProvideValue("foo", ArrayList.class));
        Assert.assertTrue(of.canProvideValue("foo", List.class));
        Assert.assertTrue(of.canProvideValue("foo", Collection.class));
        Assert.assertTrue(of.canProvideValue("foo", Object.class));
        Assert.assertFalse(of.canProvideValue("foo", String.class));

        Assert.assertEquals(list, of.provideValue("foo", List.class));
        Assert.assertEquals(list, of.provideValue("foo", Object.class));
    }

}