package br.com.binarti.sjog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import br.com.binarti.sjog.model.Person;

public class ObjectGraphPredicateTest {
	
	@Test
	public void shouldIdentifyPrimitive() {
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(boolean.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(char.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(byte.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(short.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(int.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(long.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(float.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(double.class));
		
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(EnhumTest.class));
		
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Boolean.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Character.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(String.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Byte.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Short.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Integer.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Long.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Float.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Double.class));
		
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Date.class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(Calendar.class));
	}
	
	@Test
	public void shouldIdentifyArrayOfPrimitiveType() {
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(boolean[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(char[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(byte[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(short[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(int[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(long[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(float[].class));
		assertTrue(new DefaultObjectGraphPredicate().isPrimitive(double[].class));
	}
	
	@Test
	public void shouldVerifyClassHasChild() {
		assertFalse(new DefaultObjectGraphPredicate().hasChild(int.class));
		assertFalse(new DefaultObjectGraphPredicate().hasChild(Collection.class));
		assertFalse(new DefaultObjectGraphPredicate().hasChild(Map.class));
		assertTrue(new DefaultObjectGraphPredicate().hasChild(Person.class));
	}
	
	@Test
	public void shouldIdentifyCollection() {
		assertFalse(new DefaultObjectGraphPredicate().isCollection(Object.class));
		assertFalse(new DefaultObjectGraphPredicate().isCollection(int[].class));
		assertTrue(new DefaultObjectGraphPredicate().isCollection(Collection.class));
		assertTrue(new DefaultObjectGraphPredicate().isCollection(Person[].class));
	}
	
	private static enum EnhumTest {
		Test1, Test2;
	}
	
}
