package br.com.binarti.sjog;

import static br.com.binarti.sjog.Node.ROOT_NODE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

public class ObjectGraphContext {
	
	private static final Pattern COLLECTION_NOTATION_IN_EXPR = Pattern.compile("(\\[[\\d]+\\])");
	
	private ObjectGraphPredicate predicate;
	private Set<String> includes;
	private Set<String> excludes;
	private Map<String, Boolean> autoIncludePrimitives;
		
	public ObjectGraphContext(ObjectGraphPredicate predicate, Set<String> includes, Set<String> excludes, Map<String, Boolean> autoIncludePrimitives) {
		this.predicate = predicate;
		this.includes = normalizePath(includes);
		this.excludes = normalizePath(excludes);
		this.autoIncludePrimitives = autoIncludePrimitives;
		normalizePathKeys(autoIncludePrimitives);
	}

	/**
	 * Determine if all primitive properties in node should be auto included.
	 * @param value <code>true</code> Indicates that all primitive properties in node should be included.
	 */
	public void autoIncludePrimitives(String name, boolean value) {
		autoIncludePrimitives.put(name, value);
	}
	
	/**
	 * Determine if all primitive properties in node should be auto included.
	 * @param name The node path
	 * @return <code>true</code> If all primitive properties in node should be auto included, otherwise return <code>false</code>
	 */
	public boolean autoIncludePrimitives(String name) {
		String normalizedPath = normalizePath(name);
		Boolean value = autoIncludePrimitives.get(normalizedPath);
		boolean defaultValue = true;
		return (value == null) ? defaultValue : value;
	}
	
	public ObjectGraphPredicate getPredicate() {
		return predicate;
	}
	
	private boolean isIncluded(NodePath node) {
		for (String path : includes) {
			String fullPath = normalizePath(path);
			NodePath rootedPath = NodePath.create(fullPath);
			if (rootedPath.isSame(node)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if a property is included in context.
	 * @param name Property name. Ever relative to root path
	 * @return <code>true</code> if property is included or <code>false</code> otherwise.
	 */
	public boolean included(String name, boolean isPrimitive) {
		String nameNorm = removeCollectionReferenceFromPath(normalizePath(name));
		NodePath rootedName = NodePath.create(nameNorm);
		if (isIncluded(rootedName)) {
			return true;
		}
		if (isPrimitive) {
			NodePath parent = rootedName.getParent();
			if (parent == null) {
				parent = NodePath.create(Node.ROOT_NODE);
			}
			String parentPath = parent.getPath();
			boolean autoIncludePrimitive = autoIncludePrimitives(parentPath);
			if (autoIncludePrimitive && (parentPath.equals(Node.ROOT_NODE) || includes.contains(parentPath))) {
				return true;
			}
		}
		return false;
	}
	
	private String removeCollectionReferenceFromPath(String path) {
		Matcher matcher = COLLECTION_NOTATION_IN_EXPR.matcher(path);
		if (matcher.find()) {
			return matcher.replaceAll("");
		}
		return path;
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
	
	Set<String> normalizePath(Set<String> paths) {
		Set<String> normalized = new TreeSet<>();
		for (String path : paths) {
			//do not normalize expr to exclude all primitives from root
			if (path.equals(ObjectGraphHelper.EXPR_EXCLUDE_ALL_PRIMITIVES_FROM_ROOT)) {
				normalized.add(path);
			} else {
				normalized.add(normalizePath(path));
			}
		}
		return normalized;
	}
	
	private void normalizePathKeys(Map<String, Boolean> autoIncludePrimitives) {
		Map<String, Boolean> clone = new HashMap<>(autoIncludePrimitives);
		for (Entry<String, Boolean> entry : clone.entrySet()) {
			String normalizedKey = normalizePath(entry.getKey());
			autoIncludePrimitives.remove(entry.getKey());
			autoIncludePrimitives.put(normalizedKey, entry.getValue());
		}
	}

	public boolean excluded(String name, boolean isPrimitive, boolean isRootChild) {
		String nameNorm = removeCollectionReferenceFromPath(normalizePath(name));
		NodePath rootedName = NodePath.create(nameNorm);
		for (String excluedPath : excludes) {
			//all root primitives
			if (excluedPath.equals(ObjectGraphHelper.EXPR_EXCLUDE_ALL_PRIMITIVES_FROM_ROOT) && isPrimitive && isRootChild) {
				return true;
			}
			String fullPath = normalizePath(excluedPath);
			NodePath rootedPath = NodePath.create(fullPath);
			if (rootedName.isSame(rootedPath)) {
				return true;
			} else if (rootedPath.getNode().equals("*")) {
				if (rootedPath.getParent() != null && rootedName.isSame(rootedPath.getParent())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
