package br.com.binarti.sjog;

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
	
	private boolean collection;
	private boolean collectionItem;
	private boolean insideCollection;
	private int index;
	private Class<?> type;
	
	/**
	 * Create a node path representation in graph notation.<br>
	 * 
	 * @param node Node item path
	 * @param parent Parent of the node. If node not got a parent, pass <code>null</code>
	 * @param collection Determine if this node is a collection
	 * @param collectionItem Determine if this node is a collection item
	 * @param index Index of the collection item. Use -1 if item is not a collection item.
	 */
	private NodePath(String node, NodePath parent, boolean collection, boolean collectionItem, int index) {
		this.node = node;
		this.parent = parent;
		this.collection = collection;
		this.collectionItem = collectionItem;
		this.index = index;
		determineFullPath();
	}
	
	/**
	 * Create a node path representation in graph notation.<br>
	 * 
	 * @param node Node item path
	 * @param parent Parent of the node. If node not got a parent, pass <code>null</code>
	 */
	public NodePath(String node, NodePath parent) {
		this(node, parent, false, false, -1);
	}

	private void determineFullPath() {
		if (parent == null) {
			this.path = node;
		} else {
			if (isCollectionItem()) {
				this.path = parent.getPath() + node;
			} else {
				StringJoiner fullPath = new StringJoiner(".");
				fullPath.add(parent.getPath());
				fullPath.add(node);
				this.path = fullPath.toString();
			}
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
	 * Determine whether the property represented by this path is a collection
	 */
	public boolean isCollection() {
		return collection;
	}
	
	/**
	 * Determine if this path represents a collection item
	 */
	public boolean isCollectionItem() {
		return collectionItem;
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
		List<NodePath> pathTrack = new ArrayList<>();
		NodePath current = this;
		while (current != null) {
			pathTrack.add(0, current);
			current = current.getParent();
		}
		return pathTrack.iterator();
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
			boolean collectionItem = false;
			Matcher colNotationMatcher = COLLECTION_NOTATION.matcher(node);
			if (colNotationMatcher.find()) {
				/*
				 * create a node for refer a node in collection and add the node as child
				 */
				String collectionNode = colNotationMatcher.group(1);
				if (collectionNode != null && !collectionNode.trim().isEmpty()) {
					parent = new NodePath(collectionNode, parent, true, false, -1);
				}
				collectionItem = true;
				index = Integer.parseInt(colNotationMatcher.group(2));
				node = String.format("[%d]", index);
			}
			path.add(node);
			last = new NodePath(node, parent, false, collectionItem, index);
			parent = last;
		}
		return last;
	}

	public boolean isSame(NodePath path) {
		Iterator<NodePath> thisIt = this.iterator();
		Iterator<NodePath> otherIt = path.iterator();
		boolean lastComp = false;
		while (thisIt.hasNext()) {
			NodePath pathThis = thisIt.next();
			if (!otherIt.hasNext()) {
				return lastComp;
			}
			NodePath pathOther = otherIt.next();
			if (pathOther.isCollectionItem()) {
				pathOther = otherIt.next();
			}
			lastComp = pathThis.getNode().equals(pathOther.getNode());
			if (!lastComp) {
				return false;
			}
		}
		//limit to this path. if other path contains more path node, indicates that path is not same or equivalent to this
		if (otherIt.hasNext()) {
			return false;
		}
		return lastComp;
	}
	
}
