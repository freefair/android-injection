package io.freefair.android.injection.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.freefair.android.util.function.Optional;
import io.freefair.android.util.function.Predicate;
import io.freefair.android.util.function.Predicates;
import io.freefair.android.util.logging.AndroidLogger;
import io.freefair.android.util.logging.Logger;

public class Reflection {

    private static Logger log = AndroidLogger.forClass(Reflection.class);

    public static <T> List<Field> getAllFields(Class<T> clazz) {
        return getAllFields(clazz, Predicates.<Field>alwaysTrue());
    }

    public static <T> List<Field> getAllFields(Class<T> clazz, Predicate<Field> filter) {
        return getAllFields(clazz, Optional.<Class<? super T>>empty(), filter);
    }

    public static <T> List<Field> getDeclaredFields(Class<T> clazz, Class<? super T> upToExcluding) {
        return getAllFields(clazz, Optional.<Class<? super T>>ofNullable(upToExcluding), Predicates.alwaysTrue());
    }

    public static <T> List<Field> getAllFields(Class<T> clazz, Class<? super T> upToExcluding) {
        return getAllFields(clazz, upToExcluding, Predicates.alwaysTrue());
    }

    public static <T> List<Field> getAllFields(Class<T> clazz, Class<? super T> upToExcluding, Predicate<? super Field> filter) {
        return getAllFields(clazz, Optional.<Class<? super T>>ofNullable(upToExcluding), filter);
    }

    public static <T> List<Field> getAllFields(Class<T> clazz, Optional<Class<? super T>> upToExcluding, Predicate<? super Field> filter) {

        List<Field> fields = new ArrayList<>();

        Class<?> currentClass = clazz;

        do {
            log.verbose("Now checking class " + clazz.getName());
            for (Field field : currentClass.getDeclaredFields()) {
                log.verbose("Checking field " + field.getName());
                if (filter.test(field)) {
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        while (currentClass != null && !(upToExcluding.isPresent() && currentClass.equals(upToExcluding.orNull())));

        return fields;
    }
}
