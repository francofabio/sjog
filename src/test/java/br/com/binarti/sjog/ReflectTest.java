package br.com.binarti.sjog;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import br.com.binarti.sjog.model.PojoWithAllPrimitivesAndWrappers;

public class ReflectTest {

	private boolean containsGetter(List<Method> getters, String propertyName) {
		return getters.stream().filter(m -> Reflect.propertyName(m).equals(propertyName)).findFirst().orElse(null) != null;
	}
	
	@Test
	public void shouldListAllGetterMethodsFromPojo() throws Exception {
		List<Method> getters = Reflect.of(PojoWithAllPrimitivesAndWrappers.class).getters();
		assertEquals(20, getters.size());
		assertTrue(containsGetter(getters, "booleanPrimitive"));
		assertTrue(containsGetter(getters, "charPrimitive"));
		assertTrue(containsGetter(getters, "bytePrimitive"));
		assertTrue(containsGetter(getters, "shortPrimitive"));
		assertTrue(containsGetter(getters, "intPrimitive"));
		assertTrue(containsGetter(getters, "longPrimitive"));
		assertTrue(containsGetter(getters, "floatPrimitive"));
		assertTrue(containsGetter(getters, "doublePrimitive"));
		assertTrue(containsGetter(getters, "enumAsPrimitive"));
		assertTrue(containsGetter(getters, "booleanWrapper"));
		assertTrue(containsGetter(getters, "characterWrapper"));
		assertTrue(containsGetter(getters, "stringWrapper"));
		assertTrue(containsGetter(getters, "byteWrapper"));
		assertTrue(containsGetter(getters, "shortWrapper"));
		assertTrue(containsGetter(getters, "integerWrapper"));
		assertTrue(containsGetter(getters, "longWrapper"));
		assertTrue(containsGetter(getters, "floatWrapper"));
		assertTrue(containsGetter(getters, "doubleWrapper"));
		assertTrue(containsGetter(getters, "date"));
		assertTrue(containsGetter(getters, "calendar"));
	}
	
}
