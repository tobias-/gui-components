package com.sourceforgery.nongui;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TimeZone;
import java.util.UUID;

public class BeanUtil {

	private BeanUtil() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T valueOf(final Class<T> clazz, final Object o) {

		try {
			T result = null;
			if (o != null) {
				if (clazz == UUID.class) {
					result = (T) UUID.fromString(o.toString());
				} else if (clazz == Dimension.class) {
					String[] split = o.toString().split("x");
					result = (T) new Dimension(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
				} else if (clazz == TimeZone.class) {
					result = (T) TimeZone.getTimeZone(o.toString());
				} else {
					try {
						Method method = clazz.getMethod("valueOf", o.getClass());
						result = clazz.cast(method.invoke(null, o));
					} catch (InvocationTargetException e)  {
						Constructor<T> c = clazz.getConstructor(o.getClass());
						result = c.newInstance(o);
					} catch (RuntimeException e) {
						Constructor<T> c = clazz.getConstructor(o.getClass());
						result = c.newInstance(o);
					} catch (NoSuchMethodException e) {
						Constructor<T> c = clazz.getConstructor(o.getClass());
						result = c.newInstance(o);
					}
				}
			}
			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error running " + clazz.getName() + ".valueOf((" + o.getClass() + ") " + o
					+ ")", e);
		}
	}

	public static String toString(final Object value, final Class<?> clazz) {
		if (value == null || value instanceof String) {
			return (String) value;
		} else {
			if (Dimension.class == clazz) {
				return "" + ((Dimension)value).width + 'x' + ((Dimension)value).height;
			} else {
				return value.toString();
			}
		}
	}
}