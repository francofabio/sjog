package br.com.binarti.sjog;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class NodePathTest {

	@Test
	public void shouldCreateRootNodePath() {
		NodePath path = new NodePath("$root", null);
		assertEquals("$root", path.getNode());
		assertTrue(path.isRoot());
	}
	
	@Test
	public void shouldCreateNodePathWithParent() {
		NodePath root = new NodePath("person", null);
		NodePath child = new NodePath("child", root);
		assertEquals(root, child.getParent());
		assertEquals("person.child", child.getPath());
	}
	
	@Test
	public void shouldCreateNodePathFromExpressionSingle() {
		NodePath path = NodePath.create("name");
		assertEquals("name", path.getNode());
	}
	
	@Test
	public void shouldCreateNodePathFromExpression() {
		NodePath path = NodePath.create("person.name");
		assertEquals("name", path.getNode());
		assertEquals("person", path.getParent().getNode());
		assertEquals("person.name", path.getPath());
	}
	
	@Test
	public void shouldIterateInPath() {
		NodePath path = NodePath.create("person.address.city.name");
		Iterator<NodePath> it = path.iterator();
		assertEquals("person", it.next().getNode());
		assertEquals("address", it.next().getNode());
		assertEquals("city", it.next().getNode());
		assertEquals("name", it.next().getNode());
	}
	
	@Test
	public void shouldCompareNodePath() {
		NodePath path1 = NodePath.create("name");
		NodePath path2 = NodePath.create("name");
		assertEquals(path1, path2);
	}
	
	@Test
	public void shouldCompareWithNull() {
		NodePath path1 = NodePath.create("name");
		assertNotEquals(path1, null);
	}
	
	@Test
	public void shouldCompareWithDiferenteClass() {
		NodePath path1 = NodePath.create("name");
		assertNotEquals(path1, new Object());
	}
	
	@Test
	public void shouldCompareWithPathNull() {
		NodePath path1 = new NodePath(null, null);
		NodePath path2 = NodePath.create("name");
		assertNotEquals(path1, path2);
	}
	
	@Test
	public void shouldIdentifyCollectionExpression() {
		NodePath path = NodePath.create("itens[0].id");
		assertTrue(path.getParent().getParent().isCollection());
		assertTrue(path.getParent().isCollectionItem());
		assertEquals(0, path.getParent().getIndex());
		assertEquals("id", path.getNode());
	}
	
}
