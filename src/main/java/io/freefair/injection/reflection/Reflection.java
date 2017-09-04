package io.freefair.injection.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.freefair.util.function.Optional;
import io.freefair.util.function.Predicate;
import io.freefair.util.function.Predicates;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Reflection {

    public static <T> List<Field> getAllFields(Class<T> clazz) {
        return getAllFields(clazz, Predicates.<Field>alwaysTrue());
    }

    public static <T> List<Field> getAllFields(Class<T> clazz, Predicate<Field> filter) {
        return getAllFields(clazz, Optional.<Class<? super T>>empty(), filter);
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
            log.debug("Now checking class {}", currentClass.getName());
            for (Field field : currentClass.getDeclaredFields()) {
                log.trace("Checking field {}", field.getName());
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
