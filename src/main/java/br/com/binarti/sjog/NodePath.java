package br.com.binarti.sjog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a node path
 * 
 * @author francofabio
 * 
 */
public class NodePath {

	private static final Pattern COLLECTION_NOTATION = Pattern.compile("^(.*)\\[([\\d]+)\\]$");
	
	private String node;
	private String path;
	private NodePath parent;
	
	private Method method;
	private boolean collection;
	private boolean insideCollection;
	private int index;
	private Class<?> type;
	private Object value;
	private Object firstNonNullValueInCollection;
	
	/**
	 * Create a node path representation in graph notation.<br>
	 * 
	 * @param node Node item path
	 * @param parent Parent of the node. If node not got a parent, pass <code>null</code>
	 */
	public NodePath(String node, NodePath parent) {
		this.node = node;
		this.parent = parent;
		determineFullPath();
	}

	private void determineFullPath() {
		if (parent == null) {
			this.path = node;
		} else {
			StringJoiner fullPath = new StringJoiner(".");
			fullPath.add(parent.getPath());
			fullPath.add(node);
			this.path = fullPath.toString();
		}
	}
	
	/**
	 * Node item path
	 * @return Node item path
	 */
	public String getNode() {
		return node;
	}
	
	/**
	 * Full node path, from parent to node
	 * @return Full node path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Parent of the node
	 * @return Parent of the node
	 */
	public NodePath getParent() {
		return parent;
	}
	
	/**
	 * Get method reference to access property represented by this path
	 */
	public Method getMethod() {
		return method;
	}

	void setMethod(Method method) {
		this.method = method;
	}
	
	/**
	 * Determine whether the property represented by this path is a collection
	 */
	public boolean isCollection() {
		return collection;
	}
	
	void setCollection(boolean collection) {
		this.collection = collection;
	}
	
	/**
	 * Determine whether the property is inside a collection
	 */
	public boolean isInsideCollection() {
		return insideCollection;
	}
	
	void setInsideCollection(boolean insideCollection) {
		this.insideCollection = insideCollection;
	}
	
	/**
	 * Index of the item in collection. Used only if the path element represent a access by index in element item.</br>
	 * Like: <code>collection[0]</code> 
	 */
	public int getIndex() {
		return index;
	}
	
	void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Get type of the property represented by this path, could be null
	 * @return Type of the property represented by this path, could be null
	 */
	public Class<?> getType() {
		return type;
	}
	
	void setType(Class<?> type) {
		this.type = type;
	}
	
	/**
	 * Get value of the property representend by this path, could be null
	 * @return Value of the property representend by this path, could be null
	 */
	public Object getValue() {
		return value;
	}
	
	void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * Get first non null element in collection. This attribute are is fill when this path represents a collection.
	 * @return First non null element in collection. This attribute are is fill when this path represents a collection.
	 */
	public Object getFirstNonNullValueInCollection() {
		return firstNonNullValueInCollection;
	}
	
	void setFirstNonNullValueInCollection(Object firstNonNullValueInCollection) {
		this.firstNonNullValueInCollection = firstNonNullValueInCollection;
	}
	
	/**
	 * Determine whether this element is root of the path
	 * @return <code>true</code>If this element is root, otherwise return <code>false</code> 
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	/**
	 * Iterator for node path, from root parent to node
	 * @return Iterator for node path, from root parent to node
	 */
	public Iterator<NodePath> iterator() {
		List<NodePath> pathTrace = new ArrayList<>();
		NodePath current = this;
		while (current != null) {
			pathTrace.add(0, current);
			current = current.getParent();
		}
		return pathTrace.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePath other = (NodePath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public static NodePath create(String fullPath) {
		String[] nodes = fullPath.split("[.]");
		StringJoiner path = new StringJoiner(".");
		NodePath parent = null;
		NodePath last = null;
		for (String node : nodes) {
			int index = -1;
			boolean collection = false;
			Matcher colNotationMatcher = COLLECTION_NOTATION.matcher(node);
			if (colNotationMatcher.find()) {
				collection = true;
				node = colNotationMatcher.group(1);
				index = Integer.parseInt(colNotationMatcher.group(2));
			}
			path.add(node);
			last = new NodePath(node, parent);
			last.setCollection(collection);
			last.setIndex(index);
			parent = last;
		}
		return last;
	}
	
}
