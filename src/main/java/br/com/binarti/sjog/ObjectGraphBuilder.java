package br.com.binarti.sjog;

import static br.com.binarti.sjog.Node.ROOT_NODE;

import java.util.HashMap;
import java.util.Map;
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

	private ObjectGraphPredicate predicate;
	private Set<String> includes;
	private Set<String> excludes;
	private Map<String, Boolean> autoIncludePrimitives;
	
	public ObjectGraphBuilder(ObjectGraphPredicate objectGraphPredicate) {
		this.predicate = objectGraphPredicate;
		this.includes = new TreeSet<>();
		this.excludes = new TreeSet<>();
		this.autoIncludePrimitives = new HashMap<>();
	}
	
	public ObjectGraphBuilder() {
		this(getDefaultObjectGraphPredicate());
	}
	
	/**
	 * Include property in object graph.<br/>
	 * If type of property is a non primitive object, collection or map then all primitive properties are included in object graph
	 * @param name Property name to include. Could be nested property
	 */
	public ObjectGraphBuilder include(String name) {
		if (!includes.contains(name)) {
			includes.add(name);
		}
		return this;
	}
	
	/**
	 * Determine if all primitive properties in root node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in root node should be included.
	 */
	public ObjectGraphBuilder autoIncludePrimitivesFromRoot(boolean value) {
		autoIncludePrimitives.put(ROOT_NODE, value);
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
		return exclude(ObjectGraphHelper.EXPR_EXCLUDE_ALL_PRIMITIVES_FROM_ROOT);
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
	 * Create a object graph context using configured data in this builder
	 * @return Object graph context with included, excluded and auto include primitives configuration in this builder
	 */
	public ObjectGraphContext buildContext() {
		return new ObjectGraphContext(predicate, includes, excludes, autoIncludePrimitives);
	}
	
	/**
	 * Build an object graph for a given object</br>
	 * 
	 * @param root The object
	 * @return An object graph for a given object
	 */
	public ObjectGraph build(Object root) {
		ObjectGraphContext context = buildContext();
		ObjectGraph objectGraph = new ObjectGraph(root, context);
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
