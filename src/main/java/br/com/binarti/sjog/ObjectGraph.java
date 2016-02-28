package br.com.binarti.sjog;

import static br.com.binarti.sjog.Node.ROOT_NODE;

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
	
	private Node rootNode;
	private Object obj;
	private ObjectGraphContext context;
	
	ObjectGraph(Object obj, ObjectGraphContext context) {
		this.obj = obj;
		boolean isRootCollection = false;
		if (obj != null) {
			isRootCollection = context.getPredicate().isCollection(obj.getClass());
		}
		this.rootNode = new Node(ROOT_NODE, null, NodePath.create(ROOT_NODE), obj, context, isRootCollection);
		this.context = context;
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
	 * Returns all children node.<br/>
	 * If this node is a collection, return all node representing all collection items
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
	
	/**
	 * Get property value from object in graph
	 * @param name Property name
	 * @return Property value
	 */
	public Object get(String name) {
		if (obj == null) {
			return null;
		}
		NodePath path = NodePath.create(context.normalizePath(name));
		Iterator<NodePath> it = path.iterator();
		Object parent = obj;
		Object value = null;
		Node nodeParent = rootNode;
		while (it.hasNext()) {
			NodePath curPath = it.next();
			Node node;
			//For null parent return null value
			if (parent == null) {
				return null;
			}
			node = nodeParent.getChild(curPath.getNode());
			if (node == null) {
				if (curPath.isCollectionItem()) {
					Object collection = parent;
					if ((curPath.getIndex() < 0 || curPath.getIndex() >= ObjectGraphHelper.getCollectionSize(collection))) {
						throw new IndexOutOfBoundsException("Unreachable collection item: " + curPath.getPath());
					}
				} else {
					throw new ObjectGraphException("Property " + curPath.getNode() + " not found or not accessible");
				}
			}
			if (curPath.isCollection() && !node.isCollection()) {
				throw new ObjectGraphException("Property " + curPath.getNode() + " is not a collection");
			}
			value = node.getValue();
			nodeParent = node;
			parent = value;
		}
		return value;
	}

	/**
	 * Get collection size
	 * @param name The name of collection property
	 * @return The collection size
	 */
	public int getCollectionLength(String name) {
		Object collection = get(name);
		return ObjectGraphHelper.getCollectionSize(collection);
	}
	
	/**
	 * Sort all nodes recursively by name
	 * @see Node#sortByName()
	 */
	public void sortByName() {
		rootNode.sortByName();
	}

}
