package br.com.binarti.sjog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a node of the tree graph
 * 
 * @author Fabio Franco
 */
public class Node {

	private String name;
	private NodePath path;
	private List<Node> children;
	
	/**
	 * Create a node
	 * @param name Node name
	 */
	public Node(String name, NodePath path) {
		if (path == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.path = path;
		this.children = new ArrayList<>();
	}
	
	/**
	 * Get name of the node
	 * @return Name of the node, generally means a property 
	 */
	public String getName() {
		return name;
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
		return Collections.unmodifiableList(children);
	}
	
	/**
	 * Determine if this node refer a collection
	 * @return <code>true</code> if this node refer a collection, <code>false</code> otherwise.
	 */
	public boolean isCollection() {
		return path.isCollection();
	}

	Node include(String name, NodePath path) {
		Node node = new Node(name, path);
		children.add(node);
		return node;
	}

	/**
	 * Get a node child by name or full path
	 * @param name Name or full path of the node
	 * @return The child node if exists, or null if not found.
	 */
	public Node getChild(String name) {
		NodePath path = NodePath.create(name);
		if (path.isRoot()) {
			return children.stream().filter(node -> {
				return node.getName() != null && node.getName().equals(name);
			}).findFirst().orElse(null);
		} else {
			Iterator<NodePath> it = path.iterator();
			Node parent = this;
			Node node = null;
			while (it.hasNext()) {
				NodePath nodePath = it.next();
				node = parent.getChild(nodePath.getNode());
				if (node == null) {
					break;
				}
				parent = node;
			}
			return node;
		}
	}
	
	/**
	 * Sort all nodes recursively by name
	 */
	public void sortByName() {
		children.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
		for (Node child : children) {
			if (!child.children.isEmpty()) {
				child.sortByName();
			}
		}
	}
	
	@Override
	public String toString() {
		return "Node [" + name + "]";
	}
	
}
