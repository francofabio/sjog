package br.com.binarti.sjog;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class ObjectGraphHelper {
	
	public static final String EXPR_EXCLUDE_ALL_PRIMITIVES_FROM_ROOT = "^.*";
	
	@SuppressWarnings("unused")
	private static final ObjectGraphHelper JUST_TO_SILENCE_COBERTURA = new ObjectGraphHelper();
	
	private ObjectGraphHelper() {
	}
	
	private static boolean isBasicJavaType(Class<?> type) {
		return type.isPrimitive() || type.isEnum() || Number.class.isAssignableFrom(type) 
				|| type.equals(String.class) || Character.class.equals(type)
				|| Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)
				|| Boolean.class.equals(type);
	}
	
	public static boolean likePrimitive(Class<?> cls) {
		return isBasicJavaType(cls) || (cls.isArray() && isBasicJavaType(cls.getComponentType()));
	}
	
	public static boolean isCollection(Class<?> cls) {
		return Collection.class.isAssignableFrom(cls) || (cls.isArray() && !likePrimitive(cls));
	}
	
	@SuppressWarnings("unchecked")
	public static int getCollectionSize(Object collection) {
		if (collection == null) {
			return 0;
		}
		if (collection instanceof Collection) {
			return ((Collection<Object>) collection).size();
		} else if (collection.getClass().isArray()) {
			return ((Object[]) collection).length;
		}
		throw new ObjectGraphException("The class type " + collection.getClass() + " is not a collection");
	}

	@SuppressWarnings("unchecked")
	public static Object getCollectonItem(Object value, int i) {
		if (value == null) {
			return null;
		}
		if (value instanceof List) {
			return ((List<Object>) value).get(i);
		} else if (value.getClass().isArray()) {
			return ((Object[]) value)[i];
		}
		throw new ObjectGraphException("The collection of type " + value.getClass() + " is not supported");
	}
	
}
