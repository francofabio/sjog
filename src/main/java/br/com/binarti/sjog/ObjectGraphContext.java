package br.com.binarti.sjog;

import static br.com.binarti.sjog.Node.ROOT_NODE;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class ObjectGraphContext {
	
	private ObjectGraphPredicate predicate;
	private Set<NodePath> includes;
	private Set<String> excludes;
	private Map<String, Boolean> autoIncludePrimitives;
		
	public ObjectGraphContext(ObjectGraphPredicate predicate, Set<NodePath> includes, Set<String> excludes, Map<String, Boolean> autoIncludePrimitives) {
		this.predicate = predicate;
		this.includes = includes;
		this.excludes = excludes;
		this.autoIncludePrimitives = autoIncludePrimitives;
	}

	/**
	 * Determine if all primitive properties in node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in node should be included.
	 */
	public void autoIncludePrimitives(String name, boolean value) {
		autoIncludePrimitives.put(name, value);
	}
	
	public boolean autoIncludePrimitives(String name) {
		Boolean value = autoIncludePrimitives.get(name);
		return (value == null) ? true : value;
	}
	
	public ObjectGraphPredicate getPredicate() {
		return predicate;
	}
	
	/**
	 * Check if a property is inclued in context.
	 * @param name Property name. Ever relative to root path
	 * @return <code>true</code> if propery is included or <code>false</code> otherwise.
	 */
	public boolean included(String name) {
		String nameNorm = normalizePath(name);
		NodePath rootedName = NodePath.create(nameNorm);
		for (NodePath path : includes) {
			String fullPath = normalizePath(path.getPath());
			NodePath rootedPath = NodePath.create(fullPath);
			if (rootedPath.isSame(rootedName)) {
				return true;
			}
		}
		return false;
	}
	
	String normalizePath(String path) {
		if (!path.startsWith(ROOT_NODE)) {
			StringJoiner fullPathNormalized = new StringJoiner(".");
			fullPathNormalized.add(ROOT_NODE);
			fullPathNormalized.add(path);
			return fullPathNormalized.toString();
		}
		return path;
	}

	public boolean excluded(String name, boolean isPrimitive, boolean isRootChild) {
		String nameNorm = normalizePath(name);
		for (String excluedPath : excludes) {
			//all root primitives
			if (excluedPath.equals(ObjectGraphHelper.EXPR_EXCLUDE_ALL_PRIMITIVES_FROM_ROOT) && isPrimitive && isRootChild) {
				return true;
			}
			String fullPath = normalizePath(excluedPath);
			if ((fullPath.equals(nameNorm)) || (fullPath.startsWith(nameNorm) && fullPath.endsWith(".*"))) {
				return true;
			}
		}
		return false;
	}
	
}
