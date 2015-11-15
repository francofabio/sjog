package br.com.binarti.sjog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represent a builder for building <code>ObjectGraph</code> instances.<br/>
 * One instance of the builder for a given class can be shared by application.
 * 
 * @author francofabio
 * 
 */
public class ObjectGraphBuilder {

	private Class<?> rootClass;
	private ObjectGraphPredicate predicate;
	private Set<NodePath> includes;
	private Set<String> excludes;
	private Map<String, Boolean> autoIncludePrimitives;
	
	public ObjectGraphBuilder(Class<?> rootClass, ObjectGraphPredicate objectGraphPredicate) {
		this.rootClass = rootClass;
		this.predicate = objectGraphPredicate;
		this.includes = new TreeSet<>((NodePath p1, NodePath p2) -> p1.getPath().compareTo(p2.getPath()));
		this.excludes = new TreeSet<>((String s1, String s2) -> s1.compareTo(s2));
		this.autoIncludePrimitives = new HashMap<>();
	}
	
	public ObjectGraphBuilder(Class<?> rootClass) {
		this(rootClass, getDefaultObjectGraphPredicate());
	}
	
	/**
	 * Include property in object graph.<br/>
	 * If type of property is a non primitive object, collection or map then all primitive properties are included in object graph
	 * @param name Property name to include. Could be nested property
	 */
	public ObjectGraphBuilder include(String name) {
		NodePath path = NodePath.create(name);
		if (!includes.contains(path)) {
			includes.add(path);
		}
		return this;
	}
	
	/**
	 * Determine if all primitive properties in root node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in root node should be included.
	 */
	public ObjectGraphBuilder autoIncludePrimitivesFromRoot(boolean value) {
		autoIncludePrimitives.put(ObjectGraph.ROOT_NODE, value);
		return this;
	}
	
	/**
	 * Determine if all primitive properties in node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in node should be included.
	 */
	public ObjectGraphBuilder autoIncludePrimitives(String name, boolean value) {
		autoIncludePrimitives.put(name, value);
		return this;
	}
	
	/**
	 * Exclude property from object graph<br/>
	 * If property pattern is '*', indicates that all given node properties should be removed.<br><br>
	 * <code>^.*</code><br>Indicates that all 'primitive' children node should be removed from root node<br><br>
	 * <code>address.*</code><br>Indicates that all children node of the address node should be removed
	 * @param name Name of the property should be removed.
	 */
	public ObjectGraphBuilder exclude(String name) {
		if (!excludes.contains(name)) {
			excludes.add(name);
		}
		return this;
	}

	/**
	 * Exclude all 'primitive' properties children of the root node
	 */
	public ObjectGraphBuilder excludePrimitivesFromRoot() {
		return exclude("^.*");
	}
	
	/**
	 * Exclude all properties children of a node. Parent property also be excluded.
	 * @param name Name of the parent property
	 * @return
	 */
	public ObjectGraphBuilder excludeAll(String name) {
		return exclude(name + ".*");
	}
	
	/**
	 * Build an object graph for a given object</br>
	 * 
	 * @param root The object
	 * @return An object graph for a given object
	 */
	public ObjectGraph build(Object root) {
		ObjectGraphMapper mapper = new ObjectGraphMapper(rootClass, root, predicate);
		//Configure auto include primitives
		for (Entry<String, Boolean> e : autoIncludePrimitives.entrySet()) {
			mapper.autoIncludePrimitives(e.getKey(), e.getValue());
		}
		mapper.expandRoot();
		for (NodePath path : includes) {
			mapper.includeAndExpand(path);
		}
		for (String name : excludes) {
			mapper.exclude(name);
		}
		boolean rootIsCollection = mapper.rootIsCollection();
		List<NodePath> allProperties = mapper.getProperties();
		ObjectGraph objectGraph = new ObjectGraph(root, rootIsCollection, allProperties.toArray(new NodePath[0]));
		return objectGraph;
	}

	private static Class<? extends ObjectGraphPredicate> defaultObjectGraphPredicateClass = DefaultObjectGraphPredicate.class;
	
	/**
	 * Get default object graph predicate
	 * @return Default object graph predicate
	 */
	public static ObjectGraphPredicate getDefaultObjectGraphPredicate() {
		try {
			return defaultObjectGraphPredicateClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not initialize default object graph predicate", e);
		}
	}
	
	/**
	 * Set default object graph predicate
	 * @param cls The class that implements ObjectGraphPredicate
	 */
	public static void setDefaultObjectGraphPredicateClass(Class<? extends ObjectGraphPredicate> cls) {
		defaultObjectGraphPredicateClass = cls;
	}
	
}
