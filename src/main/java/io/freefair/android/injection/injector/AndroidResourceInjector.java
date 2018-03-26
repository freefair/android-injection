package io.freefair.android.injection.injector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.freefair.android.injection.InjectionException;
import io.freefair.android.injection.annotation.InjectAttribute;
import io.freefair.android.injection.annotation.InjectResource;
import io.freefair.util.function.Optional;
import lombok.Getter;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static lombok.AccessLevel.PROTECTED;

abstract class AndroidResourceInjector<T> extends Injector {

    @Getter(PROTECTED)
    private final T object;

    AndroidResourceInjector(T object, Object... possibleParents) {
        super(possibleParents);
        this.object = object;
        getTopClasses().add(Application.class);
        getTopClasses().add(Fragment.class);
        try {
            getTopClasses().add(Class.forName("android.app.Fragment"));
        } catch (ClassNotFoundException ignored) {

        }
        getTopClasses().add(IntentService.class);
        getTopClasses().add(Service.class);
        getTopClasses().add(AppCompatActivity.class);
        getTopClasses().add(Activity.class);
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends T> getObjectClass() {
        return (Class<? extends T>) getObject().getClass();
    }

    @Override
    protected void visitField(@NonNull Object instance, @NonNull FieldWrapper field) {
        if (field.isAnnotationPresent(InjectResource.class)) {
            Bindings.getResourceBinding(getObjectClass()).put(field, field.getAnnotation(InjectResource.class));
        } else if (field.isAnnotationPresent(InjectAttribute.class)) {
            Bindings.getAttributeBinding(getObjectClass()).put(field, field.getAnnotation(InjectAttribute.class));
        } else {
            super.visitField(instance, field);
        }
    }

    public void injectAttributes() {

        Map<FieldWrapper, InjectAttribute> attributeBinding = Bindings.getAttributeBinding(getObjectClass());
        int[] attrIds = new int[attributeBinding.size()];

        int i = 0;
        for (Map.Entry<FieldWrapper, InjectAttribute> entry : attributeBinding.entrySet()) {
            InjectAttribute annotation = entry.getValue();
            attrIds[i] = annotation.id();

            FieldWrapper field = entry.getKey();
            if (!field.getType().isAssignableFrom(annotation.type().getClazz())) {
                logFieldTypeMissmatch(field.getField(), annotation.type().getClazz());
            }
            i++;
        }

        Optional<? extends Resources.Theme> optionalTheme = resolveBean(Resources.Theme.class, getObject());
        if (!optionalTheme.isPresent()) {
            throw new InjectionException("Theme not found");
        }

        TypedArray typedArray = optionalTheme.get().obtainStyledAttributes(attrIds);

        int index = 0;
        for (Map.Entry<FieldWrapper, InjectAttribute> entry : attributeBinding.entrySet()) {

            FieldWrapper field = entry.getKey();
            InjectAttribute annotation = entry.getValue();

            if (!field.getField().isAccessible()) {
                try {
                    field.getField().setAccessible(true);
                } catch (SecurityException e) {
                    throw new InjectionException(String.format("Failed to make field '%s' accessible", field.getField()), e);
                }
            }

            try {
                switch (annotation.type()) {
                    case BOOLEAN:
                        boolean aBoolean = typedArray.getBoolean(index, false);
                        field.getField().setBoolean(object, aBoolean);
                        break;
                    case COLOR:
                        int color = typedArray.getColor(index, 0);
                        field.getField().setInt(object, color);
                        break;
                    case COLOR_STATE_LIST:
                        ColorStateList colorStateList = typedArray.getColorStateList(index);
                        field.set(object, colorStateList);
                        break;
                    case DIMENSION:
                        float dimension = typedArray.getDimension(index, 0f);
                        field.getField().setFloat(object, dimension);
                        break;
                    case DIMENSION_PIXEL_OFFSET:
                        int dimensionPixelOffset = typedArray.getDimensionPixelOffset(index, 0);
                        field.getField().setInt(object, dimensionPixelOffset);
                        break;
                    case DIMENSION_PIXEL_SIZE:
                        int dimensionPixelSize = typedArray.getDimensionPixelSize(index, 0);
                        field.getField().setInt(object, dimensionPixelSize);
                        break;
                    case DRAWABLE:
                        Drawable drawable = typedArray.getDrawable(index);
                        field.set(object, drawable);
                        break;
                    case FLOAT:
                        float aFloat = typedArray.getFloat(index, 0f);
                        field.getField().setFloat(object, aFloat);
                        break;
                    case FRACTION:
                        float fraction = typedArray.getFraction(index, 1, 1, 0f);
                        field.getField().setFloat(object, fraction);
                        break;
                    case INT:
                        int anInt = typedArray.getInt(index, 0);
                        field.getField().setInt(object, anInt);
                        break;
                    case INTEGER:
                        int integer = typedArray.getInteger(index, 0);
                        field.getField().setInt(object, integer);
                        break;
                    case LAYOUT_DIMENSION:
                        int layoutDimension = typedArray.getLayoutDimension(index, 0);
                        field.getField().setInt(object, layoutDimension);
                        break;
                    case RESOURCE_ID:
                        int resourceId = typedArray.getResourceId(index, 0);
                        field.getField().setInt(object, resourceId);
                        break;
                    case STRING:
                        String string = typedArray.getString(index);
                        field.set(object, string);
                        break;
                    case TEXT:
                        CharSequence text = typedArray.getText(index);
                        field.set(object, text);
                        break;
                    case TEXT_ARRAY:
                        CharSequence[] textArray = typedArray.getTextArray(index);
                        field.set(object, textArray);
                        break;
                    case TYPED_VALUE:
                        TypedValue typedValue = Optional.ofNullable((TypedValue) field.getField().get(object))
                                .orElse(new TypedValue());
                        typedArray.getValue(index, typedValue);
                        field.set(object, typedValue);
                        break;
                    default:
                        throw new InjectionException(String.format("Unkown resource type at field '%s'", field.getField()));
                }
            } catch (IllegalAccessException iae) {
                throw new InjectionException(String.format("Failed to inject '%s' attribute into field '%s'", annotation.type(), field.getField()), iae);
            }
            index++;
        }
        typedArray.recycle();
    }

    @TargetApi(LOLLIPOP)
    public void injectResources() {

        Resources resources = resolveBean(Resources.class, getObject()).orNull();
        if (resources == null) {
            throw new InjectionException("Resources.class not found");
        }

        for (Map.Entry<FieldWrapper, InjectResource> resourceBinding : Bindings.getResourceBinding(getObjectClass()).entrySet()) {
            FieldWrapper field = resourceBinding.getKey();
            InjectResource annotation = resourceBinding.getValue();
            int resourceId = annotation.id();

            if (!field.getType().isAssignableFrom(annotation.type().getClazz())) {
                logFieldTypeMissmatch(field.getField(), annotation.type().getClazz());
            }

            if (!field.getField().isAccessible()) {
                try {
                    field.getField().setAccessible(true);
                } catch (SecurityException e) {
                    throw new InjectionException(String.format("Failed to make field '%s' accessible", field.getField()), e);
                }
            }

            try {
                switch (annotation.type()) {
                    case ANIMATION:
                        XmlResourceParser animation = resources.getAnimation(resourceId);
                        field.set(object, animation);
                        break;
                    case BOOLEAN:
                        boolean aBoolean = resources.getBoolean(resourceId);
                        field.getField().setBoolean(object, aBoolean);
                        break;
                    case COLOR:
                        int color = ContextCompat.getColor(getNearestContext(getObject()), resourceId);
                        field.getField().setInt(object, color);
                        break;
                    case COLOR_STATE_LIST:
                        ColorStateList colorStateList = ContextCompat.getColorStateList(getNearestContext(getObject()), resourceId);
                        field.set(object, colorStateList);
                        break;
                    case DIMENSION:
                        float dimension = resources.getDimension(resourceId);
                        field.getField().setFloat(object, dimension);
                        break;
                    case DIMENSION_PIXEL_OFFSET:
                        int dimensionPixelOffset = resources.getDimensionPixelOffset(resourceId);
                        field.getField().setInt(object, dimensionPixelOffset);
                        break;
                    case DIMENSION_PIXEL_SIZE:
                        int dimensionPixelSize = resources.getDimensionPixelSize(resourceId);
                        field.getField().setInt(object, dimensionPixelSize);
                        break;
                    case DRAWABLE:
                        Drawable drawable = ContextCompat.getDrawable(getNearestContext(getObject()), resourceId);
                        field.set(object, drawable);
                        break;
                    case FRACTION:
                        float fraction = resources.getFraction(resourceId, 1, 1);
                        field.getField().setFloat(object, fraction);
                        break;
                    case INT_ARRAY:
                        int[] intArray = resources.getIntArray(resourceId);
                        field.set(object, intArray);
                        break;
                    case INTEGER:
                        int integer = resources.getInteger(resourceId);
                        field.getField().setInt(field, integer);
                        break;
                    case LAYOUT:
                        XmlResourceParser layout = resources.getLayout(resourceId);
                        field.set(object, layout);
                        break;
                    case MOVIE:
                        Movie movie = resources.getMovie(resourceId);
                        field.set(object, movie);
                        break;
                    case STRING:
                        String string = resources.getString(resourceId);
                        field.set(object, string);
                        break;
                    case STRING_ARRAY:
                        String[] stringArray = resources.getStringArray(resourceId);
                        field.set(object, stringArray);
                        break;
                    case TEXT:
                        CharSequence text = resources.getText(resourceId);
                        field.set(object, text);
                        break;
                    case TEXT_ARRAY:
                        CharSequence[] textArray = resources.getTextArray(resourceId);
                        field.set(object, textArray);
                        break;
                    case TYPED_VALUE:
                        TypedValue typedValue = Optional.ofNullable((TypedValue) field.getField().get(object))
                                .orElse(new TypedValue());
                        resources.getValue(resourceId, typedValue, true);
                        field.set(object, typedValue);
                        break;
                    case XML:
                        XmlResourceParser xml = resources.getXml(resourceId);
                        field.set(object, xml);
                        break;
                    default:
                        throw new InjectionException(String.format("Unkown resource type '%s' at field %s", annotation.type(), field.getField()));
                }
            } catch (IllegalAccessException iae) {
                throw new InjectionException(String.format("Failed to inject '%s' resource into field '%s'", annotation.type(), field.getField()), iae);
            }
        }
    }

    private void logFieldTypeMissmatch(Field field, Class<?> clazz) {
        Log.w(AndroidResourceInjector.class.getSimpleName(), "Possible field type missmatch at Field " + field.toString() + ". Going to inject type " + clazz.getName() + " but Field type is " + field.getType().getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public <B> Optional<? extends B> resolveBean(@NonNull Class<B> type, Object instance) {

        if (type.isAssignableFrom(getObjectClass())) {
            return Optional.of((B) getObject());
        }

        if (type.isAssignableFrom(Resources.Theme.class)) {
            Context context = getNearestContext(instance);
            return Optional.of((B) context.getTheme());
        }

        if (type.isAssignableFrom(Resources.class)) {
            Context context = getNearestContext(instance);
            return Optional.of((B) context.getResources());
        }

        return super.resolveBean(type, instance);
    }

    protected Context getNearestContext(Object instance) {
        Context context;
        if (Context.class.isAssignableFrom(getObjectClass())) {
            context = (Context) getObject();
        } else {
            context = resolveBean(Context.class, instance).orNull();
        }
        return context;
    }

    private static class Bindings {
        private static WeakHashMap<Class<?>, Map<FieldWrapper, InjectAttribute>> attributeBindings = new WeakHashMap<>();
        private static WeakHashMap<Class<?>, Map<FieldWrapper, InjectResource>> resourceBindings = new WeakHashMap<>();

        @NonNull
        static Map<FieldWrapper, InjectAttribute> getAttributeBinding(Class<?> clazz) {
            if (!attributeBindings.containsKey(clazz)) {
                attributeBindings.put(clazz, new HashMap<FieldWrapper, InjectAttribute>());
            }
            return attributeBindings.get(clazz);
        }

        @NonNull
        static Map<FieldWrapper, InjectResource> getResourceBinding(Class<?> clazz) {
            if (!resourceBindings.containsKey(clazz)) {
                resourceBindings.put(clazz, new HashMap<FieldWrapper, InjectResource>());
            }
            return resourceBindings.get(clazz);
        }
    }
}
