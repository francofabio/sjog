package br.com.binarti.sjog;

import java.util.Collection;
import java.util.Map;

public class DefaultObjectGraphPredicate implements ObjectGraphPredicate {
	
	@Override
	public boolean isPrimitive(Class<?> cls) {
		return ObjectGraphHelper.likePrimitive(cls);
	}

	@Override
	public boolean hasChild(Class<?> cls) {
		return !isPrimitive(cls) && !Collection.class.isAssignableFrom(cls) && !Map.class.isAssignableFrom(cls);
	}
	
	@Override
	public boolean isCollection(Class<?> cls) {
		return ObjectGraphHelper.isCollection(cls);
	}

}
