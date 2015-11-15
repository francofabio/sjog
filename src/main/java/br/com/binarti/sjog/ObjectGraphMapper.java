package br.com.binarti.sjog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Mapper a object expanding and resolving your nested properties. 
 * 
 * @author francofabio
 *
 */
public class ObjectGraphMapper {

	private Object object;
	private Class<?> objectClass;
	private ObjectGraphPredicate predicate;
	private List<NodePath> propertiesPath;
	private Map<String, Boolean> autoIncludePrimitives;
	
	public ObjectGraphMapper(Class<?> objectClass, Object object, ObjectGraphPredicate predicate) {
		this.objectClass = objectClass;
		this.object = object;
		this.predicate = predicate;
		this.propertiesPath = new ArrayList<>();
		this.autoIncludePrimitives = new HashMap<>();
	}
	
	private NodePath get(String path) {
		return propertiesPath.stream().filter(node -> node.getPath().equals(path)).findFirst().orElse(null);
	}
	
	@SuppressWarnings("rawtypes")
	private Object firstNonNullValueInCollection(Object collection) {
		if (collection == null) {
			return null;
		}
		if (collection instanceof List) {
			List list = (List) collection;
			//first non null element
			for (Object e : list) {
				if (e != null) {
					return e;
				}
			}
			return null;
		} else if (collection.getClass().isArray()) {
			Object[] array = (Object[]) collection;
			//first non null element
			for (Object e : array) {
				if (e != null) {
					return e;
				}
			}
			return null;
		} else {
			throw new ObjectGraphException("Unsupported collection type " + collection.getClass());
		}
	}
	
	/**
	 * Determine if root object is a collection
	 * @return <code>true></code> if root object is a collection, otherwise return <code>false</code>
	 */
	public boolean rootIsCollection() {
		return (object != null && predicate.isCollection(object.getClass()));
	}
	
	/**
	 * Determine if all primitive properties in node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in node should be included.
	 */
	public void autoIncludePrimitives(String name, boolean value) {
		autoIncludePrimitives.put(name, value);
	}
	
	private boolean autoIncludePrimitives(String name) {
		Boolean value = autoIncludePrimitives.get(name);
		return (value == null) ? true : value;
	}
	
	/**
	 * Include property in mapper all 'primitives'
	 * {@link ObjectGraphPredicate#isPrimitive(Class)} properties
	 * 
	 */
	public void expandRoot() {
		boolean insideCollection = false;
		Object parent = object;
		Class<?> parentType = objectClass;
		
		//When object is a collection. Determine the type of collection to use as root object
		if (rootIsCollection()) {
			insideCollection = true;
			Object firstVal = firstNonNullValueInCollection(object);
			if (firstVal != null) {
				parent = firstVal;
				parentType = firstVal.getClass();
			} else {
				return; //impossible to determine properties of a empty collection
			}
		}
		Class<?> type = getType(parent, parentType);
		if (predicate.hasChild(type)) {
			includePrimitives(parentType, parent, null, insideCollection);
		}
	}
	
	/**
	 * Include property in mapper. If property contains children
	 * {@link ObjectGraphPredicate#hasChild(Class)} all 'primitives'
	 * {@link ObjectGraphPredicate#isPrimitive(Class)} are included (expands)
	 * 
	 * @param path Path for property
	 */
	public ObjectGraphMapper includeAndExpand(NodePath path) {
		boolean insideCollection = false;
		Iterator<NodePath> it = path.iterator();
		Object parent = object;
		Class<?> parentType = objectClass;
		
		//When object is a collection. Determine the type of collection to use as root object
		if (rootIsCollection()) {
			Object firstVal = firstNonNullValueInCollection(object);
			if (firstVal != null) {
				insideCollection = true;
				parent = firstVal;
				parentType = firstVal.getClass();
			} else {
				return this; //impossible to determine properties of a empty collection
			}
		}
		while (it.hasNext()) {
			NodePath currentPath = it.next();
			NodePath storedPath = get(currentPath.getPath());
			if (storedPath == null) {
				Class<?> resolvedParentType = getType(parent, parentType);
				//when parent is object should not include children
				if (resolvedParentType == Object.class) {
					break;
				}
				Method method = Reflect.of(resolvedParentType).getter(currentPath.getNode());
				if (method == null) {
					throw new IllegalArgumentException("Property " + currentPath.getNode() + " not found in class " + resolvedParentType);
				}
				Object value = null;
				if (parent != null) {
					value = Reflect.invoke(method, parent, currentPath.getNode());
				}
				Class<?> type = getType(value, method.getReturnType());
				currentPath.setInsideCollection(insideCollection);
				//method are used only object is in inside a collection. When object is outside collection the value is set in value property of node path
				if (insideCollection) {
					currentPath.setMethod(method);
				}
				//when inside in collection, each value are resolved at getter time
				if (!insideCollection) {
					currentPath.setValue(value);
				}
				parent = value;
				parentType = type;
				if (predicate.isCollection(type)) {
					propertiesPath.add(currentPath);
					insideCollection = true;
					currentPath.setCollection(true);
					//when null collection, could not be possible to determine the type of collection
					if (value != null) {
						Object firstVal = firstNonNullValueInCollection(value);
						if (firstVal != null) {
							Class<?> collectionType = getType(firstVal, void.class);
							if (predicate.hasChild(collectionType) && !it.hasNext()) {
								includePrimitives(collectionType, firstVal, currentPath, insideCollection);
							}
							currentPath.setFirstNonNullValueInCollection(firstVal);
							parent = firstVal;
							parentType = firstVal.getClass();
						} else { //when is a empty collection, could not be possible to determine the type of collection
							break;
						}
					} else {
						break;
					}
				} else {
					if (predicate.hasChild(type)) {
						//Includes in order
						propertiesPath.add(currentPath);
						if (!it.hasNext()) { //include children only if is the last element in path
							includePrimitives(type, value, currentPath, insideCollection);
						}
					} else {
						propertiesPath.add(currentPath);
					}
				}
				currentPath.setType(parentType);
			} else {
				if (storedPath.isCollection()) { //When path refer a collection, ever use collection type
					parent = storedPath.getFirstNonNullValueInCollection();
					insideCollection = true;
				} else {
					parent = storedPath.getValue();
				}
				parentType = storedPath.getType();
			}
		}
		return this;
	}

	/**
	 * Get object type or default type if object is null.
	 * 
	 * @param value Then object
	 * @param defaultType Default type, used if object is null
	 * @return Object type or default type if object is null
	 */
	private Class<?> getType(Object value, Class<?> defaultType) {
		return (value != null) ? value.getClass() : defaultType;
	}
	
	/**
	 * Include primitive properties of the parent.<br>
	 * Primitive properties are included only if is allowed in autoIncludePrimitives {@link #autoIncludePrimitives(String)}
	 * 
	 * @param parentType The type of the parent
	 * @param parentValue The parent value
	 * @param parentNode Parent node path
	 * @param insideCollection Indicate if the parent is inside a collection
	 */
	private void includePrimitives(Class<?> parentType, Object parentValue, NodePath parentNode, boolean insideCollection) {
		String parentPath;
		if (parentNode == null) {
			parentPath = ObjectGraph.ROOT_NODE;
		} else {
			parentPath = parentNode.getPath();
		}
		if (!autoIncludePrimitives(parentPath)) {
			return;
		}
		Reflect.of(parentType).getters().forEach(getter -> {
			Class<?> type = getter.getReturnType();
			if (predicate.isPrimitive(type)) {
				String propertyName = Reflect.propertyName(getter);
				NodePath path = new NodePath(propertyName, parentNode);
				if (get(path.getPath()) == null) {
					Object value = null;
					if (parentValue != null) {
						 value = Reflect.invoke(getter, parentValue, propertyName);
					}
					path.setMethod(getter);
					path.setInsideCollection(insideCollection);
					path.setType(type);
					if (!insideCollection) {
						path.setValue(value);
					}
					propertiesPath.add(path);
				}
			}
		});
	}

	/**
	 * Exclude property from object graph<br/>
	 * If property pattern is '*', indicates that all give node properties should be removed.<br><br>
	 * <code>^*</code><br>Indicates that all 'primitive' children node should be removed from root node<br><br>
	 * <code>address.*</code><br>Indicates that all children node of the address node should be removed
	 * @param name Name of the property should be removed.
	 */
	public void exclude(String name) {
		//remove all 'primitive' nodes
		if (name.equals("^.*")) {
			Iterator<NodePath> it = propertiesPath.iterator();
			while (it.hasNext()) {
				NodePath path = it.next();
				if (path.getParent() == null && predicate.isPrimitive(path.getType())) {
					it.remove();
				}
			}
		} else if (name.endsWith(".*")) { //remove all children from node
			String parentNode = name.substring(0, name.length()-2);
			Pattern parentNodePattern = Pattern.compile("(^" + parentNode + "\\..*)|(^" + parentNode + "$)");
			propertiesPath.removeIf(e -> {
				return parentNodePattern.matcher(e.getPath()).find();
			});
		} else {
			NodePath node = NodePath.create(name);
			propertiesPath.remove(node);
		}
	}
	
	/**
	 * Get all properties of the mapper
	 * @return All properties of the mapper
	 */
	public List<NodePath> getProperties() {
		return Collections.unmodifiableList(propertiesPath);
	}
	
}
