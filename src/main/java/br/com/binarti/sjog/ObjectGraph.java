package br.com.binarti.sjog;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a object graph in a given moment.<br/>
 * A object graph represents the properties nodes that can be accessed by the property name  or an expression.
 *  
 * @author francofabio
 *
 */
public class ObjectGraph {

	static final String ROOT_NODE = "$root";
	
	private Node rootNode;
	private Object obj;
	
	ObjectGraph(Object obj, boolean rootIsCollection, NodePath...allowedPaths) {
		this.obj = obj;
		this.rootNode = new Node(ROOT_NODE, NodePath.create(ROOT_NODE)); //Root node
		this.rootNode.getPath().setCollection(rootIsCollection);
		include(allowedPaths);
	}

	/**
	 * Return the object of the graph
	 * @return Object of the graph
	 */
	public Object getObject() {
		return obj;
	}
	
	/**
	 * Returns the root object of the tree
	 * @return Root object
	 */
	public Node getRoot() {
		return rootNode;
	}

	/**
	 * Returns all children nodes
	 */
	public List<Node> getNodes() {
		return rootNode.getChildren();
	}

	/**
	 * Get child node by name or full path of node
	 * @param name The name or full path of node
	 * @return Found node or null, if no node are found with name or path. 
	 */
	public Node getNode(String name) {
		return rootNode.getChild(name);
	}
	
	private void include(NodePath...paths) {
		for (NodePath path : paths) {
			Node parent = rootNode;
			Iterator<NodePath> it = path.iterator();
			while (it.hasNext()) {
				NodePath nodePath = it.next();
				Node node = parent.getChild(nodePath.getNode());
				if (node == null) {
					node = parent.include(nodePath.getNode(), nodePath);
				}
				parent = node;
			}
		}
	}
	
	/**
	 * Get property value from object in graph
	 * @param name Property name
	 * @return Property value
	 */
	@SuppressWarnings("unchecked")
	public Object get(String name) {
		if (obj == null) {
			return null;
		}
		NodePath path = NodePath.create(name);
		Iterator<NodePath> it = path.iterator();
		Object parent = obj;
		Object value = null;
		Node nodeParent = rootNode;
		while (it.hasNext()) {
			NodePath curPath = it.next();
			Node node;
			boolean rootIsCollection = false;
			//When path represents root path in collection
			if (ROOT_NODE.equals(curPath.getNode()) && curPath.isCollection()) {
				node = rootNode;
				rootIsCollection = true;
			} else if (ROOT_NODE.equals(curPath.getNode())) { //When access special node $root returns root object
				node = rootNode;
				nodeParent = node;
				value = obj;
				parent = value;
				continue;
			} else {
				node = nodeParent.getChild(curPath.getNode());
			}
			if (node == null) {
				throw new ObjectGraphException("Property " + curPath.getNode() + " not found or not accessible");
			}
			if (parent == null) {
				return null;
			}
			if (curPath.isCollection() && !node.getPath().isCollection()) {
				throw new ObjectGraphException("Property " + curPath.getNode() + " is not a collection");
			}
			if (curPath.isCollection()) {
				Object collection;
				if (rootIsCollection) {
					collection = obj;
				} else {
					collection = getValue(node, parent);
				}
				if (collection == null) {
					return null;
				}
				if (collection instanceof List) {
					List<Object> list = (List<Object>) collection;
					value = list.get(curPath.getIndex());
				} else if (collection.getClass().isArray()) {
					Object[] array = (Object[]) collection;
					value = array[curPath.getIndex()];
				} else {
					throw new ObjectGraphException("The collection of type " + collection.getClass() + " not supported");
				}				
			} else {
				value = getValue(node, parent);
			}
			nodeParent = node;
			parent = value;
		}
		return value;
	}
	
	private Object getValue(Node node, Object parent) {
		if (node.getPath().isInsideCollection()) {
			return Reflect.invoke(node.getPath().getMethod(), parent, node.getPath().getPath());
		} else {
			return node.getPath().getValue();
		}
	}

	/**
	 * Get collection size
	 * @param name The name of collection property
	 * @return The collection size
	 */
	@SuppressWarnings("unchecked")
	public int getCollectionLength(String name) {
		Object collection = get(name);
		if (collection == null) {
			return 0;
		}
		if (collection instanceof Collection) {
			return ((Collection<Object>) collection).size();
		} else if (collection.getClass().isArray()) {
			return ((Object[]) collection).length;
		} else {
			throw new ObjectGraphException("The collection of type " + collection.getClass() + " not supported");
		}
	}
	
	/**
	 * Sort all nodes recursively by name
	 * @see Node#sortByName()
	 */
	public void sortByName() {
		rootNode.sortByName();
	}

}
