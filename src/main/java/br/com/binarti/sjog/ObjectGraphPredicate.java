package br.com.binarti.sjog;

public interface ObjectGraphPredicate {

	boolean isPrimitive(Class<?> cls);
	
	boolean hasChild(Class<?> cls);
	
	boolean isCollection(Class<?> cls);
	
}
