package br.com.binarti.sjog;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public final class ObjectGraphHelper {
	
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
	
}
