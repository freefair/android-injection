package io.freefair.android.injection.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import io.freefair.android.util.Predicate;

/**
 * Created by larsgrefer on 16.03.15.
 */
public class FieldAnnotationPredicate implements Predicate<Field> {

	private Class<? extends Annotation> annotation;

	public FieldAnnotationPredicate(Class<? extends Annotation> annotation){
		this.annotation = annotation;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	@Override
	public boolean test(Field input) {
		return input.isAnnotationPresent(annotation);
	}

}
