package io.freefair.android.injection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lars Grefer
 */
@Slf4j
@UtilityClass
public class Reflection {

    public static <T> List<Field> getAllFields(@NonNull Class<T> clazz) {
        return getAllFields(clazz, null);
    }

    public static <T> List<Field> getAllFields(@NonNull Class<T> clazz, @Nullable Class<? super T> upToExcluding) {
        List<Field> fields = new ArrayList<>();

        Class<?> currentClass = clazz;

        do {
            log.debug("Now checking class {}", currentClass.getName());
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        while (currentClass != null && !currentClass.equals(upToExcluding));

        return fields;
    }
}
