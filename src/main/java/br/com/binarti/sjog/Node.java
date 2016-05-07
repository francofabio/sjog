package br.com.binarti.sjog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a node of the tree graph
 * 
 * @author Fabio Franco
 */
public class Node {

	static final String ROOT_NODE = "$root";
	
	private String name;
	private NodePath path;
	private List<Node> children;
	private boolean expanded;
	private Object value;
	private ObjectGraphContext context;
	private boolean collection;
	private Node parent;
	private boolean primitive;
	
	/**
	 * Create a node
	 * @param name Node name
	 */
	public Node(String name, Node parent, NodePath path) {
		if (path == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.parent = parent;
		this.path = path;
		this.children = new ArrayList<>();
		this.expanded = false;
		this.primitive = false;
	}
	
	public Node(String name, Node parent, NodePath path, Object value, ObjectGraphContext context, boolean collection) {
		this(name, parent, path);
		this.value = value;
		this.context = context;
		this.collection = collection;
	}
	
	/**
	 * Get name of the node
	 * @return Name of the node, generally means a property 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get parent of this node.<br/>
	 * If parent is null indicates that node is root of tree
	 * @return Parent node
	 */
	public Node getParent() {
		return parent;
	}
	
	/**
	 * Get the path of the node
	 * @return Path of the node
	 */
	public NodePath getPath() {
		return path;
	}

	/**
	 * Get the children of the node
	 * @return Children of the node
	 */
	public List<Node> getChildren() {
		expand();
		return Collections.unmodifiableList(children);
	}
	
	/**
	 * Determine if this node refer a collection
	 * @return <code>true</code> if this node refer a collection, <code>false</code> otherwise.
	 */
	public boolean isCollection() {
		return collection;
	}
	
	/**
	 * Determine if this node is expanded
	 * @return <code>true</code> if node is expanded or <code>false</code> otherwise.
	 */
	public boolean isExpanded() {
		return expanded;
	}
	
	/**
	 * Determine if this node represents an primitive data
	 */
	public boolean isPrimitive() {
		return primitive;
	}
	
	/**
	 * Get node value
	 * @return Node value
	 */
	public Object getValue() {
		return value;
	}

	public Node getRoot() {
		Node root = this;
		while (root.parent != null) {
			root = root.parent;
		}
		return root;
	}
	
	private boolean isRootPath(NodePath path) {
		return (path.getPath().equals(ROOT_NODE));
	}
	
	/**
	 * Get a node child by name or full path
	 * @param name Name or full path of the node
	 * @return The child node if exists, or null if not found.
	 */
	public Node getChild(String name) {
		NodePath path = NodePath.create(name);
		if (isRootPath(path)) {
			return getRoot();
		}
		if (path.isRoot()) {
			return getChildren().stream().filter(node -> {
				return node.getName() != null && node.getName().equals(name);
			}).findFirst().orElse(null);
		} else {
			Iterator<NodePath> it = path.iterator();
			Node parent = this;
			Node node = null;
			while (it.hasNext()) {
				NodePath nodePath = it.next();
				if (isRootPath(nodePath)) {
					node = getRoot();
				} else {
					node = parent.getChild(nodePath.getNode());
				}
				if (node == null) {
					break;
				}
				parent = node;
			}
			return node;
		}
	}
	
	private static void expand(Object value, Node node, ObjectGraphContext context) {
		if (value != null && context.getPredicate().hasChild(value.getClass())) {
			Reflect.of(value.getClass()).getters().forEach(m -> {
				boolean isPrimitive = context.getPredicate().isPrimitive(m.getReturnType());
				boolean isRootChild = (node.name != null && node.name.equals(ROOT_NODE));
				String propertyName = Reflect.propertyName(m);
				StringJoiner fullPath = new StringJoiner(".");
				fullPath.add(node.path.getPath());
				fullPath.add(propertyName);
				NodePath path  = NodePath.create(fullPath.toString());
				boolean excluded = context.excluded(path.getPath(), isPrimitive, isRootChild);
				if (!excluded) {
					boolean included = context.included(path.getPath(), isPrimitive);
					if (included) {
						Node childNode = new Node(propertyName, node, path, null, context, false);
						NodeValue nodeValue = getValue(childNode, value);
						childNode.value = nodeValue.getValue();
						childNode.collection = context.getPredicate().isCollection(nodeValue.getType());
						childNode.primitive = isPrimitive;
						node.children.add(childNode);
					}
				}
			});
		}
		node.expanded = true;
	}
	
	@SuppressWarnings("rawtypes")
	private static NodeValue getValue(Node node, Object parent) {
		Method method = Reflect.of(parent.getClass()).getter(node.getName());
		if (method == null) {
			throw new ObjectGraphException("Property " + node.getName() + " not found in class " + parent.getClass() + "[" + parent + "]");
		}
		Object value = Reflect.invoke(method, parent, node.getPath().getPath());
		Class type = (value == null) ? method.getReturnType() : value.getClass();
		return new NodeValue(type, value);
	}
	
	private void expand() {
		if (!expanded) {
			if (value != null && context.getPredicate().isCollection(value.getClass())) {
				expandAsCollection();
			} else {
				expandAsObject();
			}
			expanded = true;
		}
	}
	
	private void expandAsObject() {
		expand(value, this, context);
	}
	
	private void expandAsCollection() {
		if (value != null && context.getPredicate().isCollection(value.getClass())) {
			//When value is a collection. Expand all items in collection
			if (List.class.isAssignableFrom(value.getClass()) || value.getClass().isArray()) {
				int size = ObjectGraphHelper.getCollectionSize(value);
				for (int i = 0; i < size; i++) {
					Object itemValue = ObjectGraphHelper.getCollectonItem(value, i);
					boolean isCollection = (itemValue == null) ? false : context.getPredicate().isCollection(itemValue.getClass());
					String itemPath = "["+i+"]";
					String fullPath = path.getPath() + itemPath;
					Node itemNode = new Node(itemPath, this, NodePath.create(fullPath), itemValue, context, isCollection);
					if (itemValue != null) {
						itemNode.primitive = context.getPredicate().isPrimitive(itemValue.getClass());
					}
					this.children.add(itemNode);
					expand(itemValue, itemNode, context);
				}
			} else {
				throw new ObjectGraphException("The collection of type " + value.getClass() + " not supported");
			}
		}
	}
	
	/**
	 * Sort all nodes recursively by name
	 */
	public void sortByName() {
		expand();
		children.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
		for (Node child : children) {
			child.expand();
			if (!child.children.isEmpty()) {
				child.sortByName();
			}
		}
	}
	
	@Override
	public String toString() {
		return "Node [" + name + "]";
	}
	
	private static class NodeValue {
		private Class<?> type;
		private Object value;
		
		public NodeValue(Class<?> type, Object value) {
			this.type = type;
			this.value = value;
		}
		
		public Class<?> getType() {
			return type;
		}
		
		public Object getValue() {
			return value;
		}
	}
	
}
