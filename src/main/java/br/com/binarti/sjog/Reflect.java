package br.com.binarti.sjog;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reflect {

	private static final Map<String, List<Method>> cachedMethods = new HashMap<>();
	private static final Pattern GETTER_METHOD_PATTERN = Pattern.compile("^get[\\p{Upper}]{1}.*$|^is[\\p{Upper}]{1}.*$");
	private static final Pattern EXTRACT_PROPERTY_NAME_PATTERN = Pattern.compile("^get([\\p{Upper}]{1}.*)$|^is([\\p{Upper}]{1}.*)$");
	
	private Class<?> cls;
	
	private Reflect(Class<?> cls) {
		this.cls = cls;
	}
	
	public List<Method> methods() {
		return Arrays.asList(cls.getMethods());
	}
	
	public List<Method> getters() {
		String gettersCacheKey = cls.getName() + ".getters";
		List<Method> getters = cachedMethods.get(gettersCacheKey);
		if (getters == null) {
			getters = new ArrayList<>();
			List<Method> methods = methods();
			for (Method method : methods) {
				String name = method.getName();
				if (name.equals("getClass")) continue;
				Matcher matcher = GETTER_METHOD_PATTERN.matcher(name);
				if (matcher.find()) {
					getters.add(method);
				}
			}
			removeOverridden(getters);
			cachedMethods.put(gettersCacheKey, getters);
		}
		return getters;
	}
	
	private int findMethod(List<Method> methods, Method method, int exceptIndex) {
		for (int i = 0; i < methods.size(); i++) {
			Method m = methods.get(i);
			if (m.getName().equals(method.getName()) && Arrays.equals(m.getParameterTypes(), method.getParameterTypes())) {
				if (i != exceptIndex) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private boolean isOverridden(Method m1, Method m2) {
		Class<?> declaredClassM1 = m1.getDeclaringClass();
		Class<?> declaredClassM2 = m2.getDeclaringClass();
		return declaredClassM2.isAssignableFrom(declaredClassM1);
	}
	
	private void removeOverridden(List<Method> methods) {
		List<Integer> indexToRemove = new ArrayList<>();
		List<Method> clone = new ArrayList<>(methods);
		for (int i = 0; i < clone.size(); i++) {
			if (indexToRemove.contains(i)) continue;
			Method m = clone.get(i);
			int indexFound = findMethod(clone, m, i);
			if (indexFound > -1) {
				if (isOverridden(m, clone.get(indexFound))) {
					indexToRemove.add(indexFound);
				}
			}
		}
		if (!indexToRemove.isEmpty()) {
			for (Integer index : indexToRemove) {
				methods.remove(index.intValue());
			}
		}
	}

	public Method getter(String field) {
		return getters().stream().filter(m -> propertyName(m).equals(field)).findFirst().orElse(null);
	}
	
	public static Reflect of(Class<?> cls) {
		return new Reflect(cls);
	}
	
	public static String propertyName(Method method) {
		Matcher m = EXTRACT_PROPERTY_NAME_PATTERN.matcher(method.getName());
		if (m.find()) {
			return decapitalize(firstGroupNonNull(m));
		}
		return method.getName();
	}
	
	public static String decapitalize(String name) {
		return Introspector.decapitalize(name);
	}
	
	private static String firstGroupNonNull(Matcher m) {
		for (int i=1; i <= m.groupCount(); i++) {
			String val = m.group(i);
			if (Objects.nonNull(val)) {
				return val;
			}
		}
		return null;
	}

	public static Object invoke(Method method, Object obj, String property) {
		try {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			return method.invoke(obj);
		} catch (Exception e) {
			throw new ObjectGraphException("Error while getting value of the property " + property, e);
		}
	}
	
}
