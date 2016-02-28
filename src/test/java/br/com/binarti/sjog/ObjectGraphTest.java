package br.com.binarti.sjog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.com.binarti.sjog.model.Address;
import br.com.binarti.sjog.model.City;
import br.com.binarti.sjog.model.Document;
import br.com.binarti.sjog.model.Item;
import br.com.binarti.sjog.model.Order;
import br.com.binarti.sjog.model.Page;
import br.com.binarti.sjog.model.Person;
import br.com.binarti.sjog.model.WrapperData;
import br.com.binarti.sjog.model.events.TableEvent;
import br.com.binarti.sjog.model.events.WordEvent;

public class ObjectGraphTest {

	@Test
	public void shouldCreateGraphObject() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class).build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(2, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertEquals(john, johnGraph.getObject());
		assertFalse(johnGraph.getRoot().isCollection());
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void shouldNotAllowIncludeInvalidProperty() {
//		Person john = new Person("John Smith", 30);
//		new ObjectGraphBuilder(Person.class)
//			.include("city")
//			.build(john);
//	}
	
	@Test(expected = NullPointerException.class)
	public void shouldNotAllowNodeWithPathNull() {
		new Node("test", null, null);
	}
	
	@Test
	public void shouldReturnNullIfAccessInvalidNode() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class).build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(2, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
		assertNull(johnGraph.getNode("name1"));
	}
	
	@Test
	public void shouldExcludePropertyFromGraphObject() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
				.exclude("age")
				.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(1, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
	}
	
	@Test
	public void shouldExcludeAllPrimitiveChildrenInRootNode() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
				.excludePrimitivesFromRoot()
				.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(0, johnGraph.getNodes().size());
	}
	
	@Test
	public void shouldIncludePrimitiveForNestedNode() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("spouse")
			.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(3, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertNotNull(johnGraph.getNode("spouse"));
		assertEquals(2, johnGraph.getNode("spouse").getChildren().size());
		assertNotNull(johnGraph.getNode("spouse").getChild("name"));
		assertNotNull(johnGraph.getNode("spouse").getChild("age"));
	}
	
	@Test
	public void shouldDisableAutoIncludePrimitives() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.autoIncludePrimitivesFromRoot(false)
			.include("name")
			.build(john);
		assertNotNull(johnGraph);
		assertEquals(1, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
	}
	
	@Test
	public void shouldDisableAutoIncludePrimitivesForNestedNode() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.autoIncludePrimitives("spouse", false)
			.include("spouse")
			.include("spouse.name")
			.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		
		assertEquals(3, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertNotNull(johnGraph.getNode("spouse"));
		
		assertEquals(1, johnGraph.getNode("spouse").getChildren().size());
		assertNotNull(johnGraph.getNode("spouse").getChild("name"));
	}
	
	@Test
	public void shouldAccessNestedPropertyUsingGraphNotation() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("spouse")
			.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(johnGraph.getNodes().size(), 3);
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertNotNull(johnGraph.getNode("spouse"));
		assertEquals(2, johnGraph.getNode("spouse").getChildren().size());
		assertNotNull(johnGraph.getNode("spouse.name"));
		assertEquals("$root.spouse.name", johnGraph.getNode("spouse.name").getPath().getPath());
		assertNotNull(johnGraph.getNode("spouse.age"));
		assertEquals("$root.spouse.age", johnGraph.getNode("spouse.age").getPath().getPath());
	}
	
	@Test
	public void shouldExcludeAllChildrenInNestedNode() {
		Person john = new Person("John Smith", 30, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
				.include("spouse.name")
				.include("spouse.age")
				.excludeAll("spouse")
				.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(2, johnGraph.getNodes().size());
	}
	
	@Test
	public void shouldIncludeNestedPropertyDirect() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		john.setAddress(new Address(new City("Cariacica", "ES"), "29153040"));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("address.zip")
			.build(john);
		assertNotNull(johnGraph);
		assertNotNull(johnGraph.getRoot());
		assertEquals(johnGraph.getNodes().size(), 3);
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertNotNull(johnGraph.getNode("address"));
		assertEquals(1, johnGraph.getNode("address").getChildren().size());
		assertNotNull(johnGraph.getNode("address.zip"));
	}
	
	@Test
	public void shouldGetRootNodeFromExpression() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class).build(john);
		assertNotNull(johnGraph);
		assertEquals(2, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("$root"));
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertEquals(johnGraph.getRoot(), johnGraph.getNode("$root"));
	}
	
	@Test
	public void shouldIncludeNestedPropertyFromObjectInCollectionInferCollectionType() {
		Order order = new Order(129, new Date(), 1650d);
		order.addItem(new Item(1, "MacBook pro 13"));
		order.addItem(new Item(2, "iPad Air 2"));
		order.addItem(new Item(3, "iPad Mini 1"));
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(4, orderGraph.getNodes().size());
		assertNotNull(orderGraph.getNode("id"));
		assertNotNull(orderGraph.getNode("date"));
		assertNotNull(orderGraph.getNode("amount"));
		assertNotNull(orderGraph.getNode("itens"));
		assertEquals(3, orderGraph.getNode("itens").getChildren().size());
		assertNotNull(orderGraph.getNode("itens[0].id"));
		assertNotNull(orderGraph.getNode("itens[0].productName"));
	}
	
	@Test
	public void shouldGetDataFromObjectGraph() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class).build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
	}
	
	@Test
	public void shouldGetDataFromObjectGraphUsingNestedProperty() {
		Person john = new Person("John Smith", 30);
		john.setAddress(new Address(new City("Cariacica", "ES"), "29153040"));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
				.include("address")
				.include("address.city")
				.build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
		assertEquals(john.getAddress(), johnGraph.get("address"));
		assertEquals(john.getAddress().getZip(), johnGraph.get("address.zip"));
		assertEquals(john.getAddress().getCity().getName(), johnGraph.get("address.city.name"));
		assertEquals(john.getAddress().getCity().getState(), johnGraph.get("address.city.state"));
	}
	
	@Test
	public void shouldGetDataFromCollectionInObjectGraph() {
		Order order = new Order(129, new Date(), 1650d);
		order.addItem(new Item(1, "MacBook pro 13"));
		order.addItem(new Item(2, "iPad Air 2"));
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertEquals(order.getItens().get(0).getId(), orderGraph.get("itens[0].id"));
		assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("itens[0].productName"));
		assertEquals(order.getItens().get(1).getId(), orderGraph.get("itens[1].id"));
		assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("itens[1].productName"));
	}
	
	@Test
	public void shouldGetDataFromCollectionInObjectGraphAsArray() {
		Order order = new Order(129, new Date(), 1650d);
		order.addItem(new Item(1, "MacBook pro 13"));
		order.addItem(new Item(2, "iPad Air 2"));
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itensAsArray")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertEquals(order.getItens().get(0).getId(), orderGraph.get("itensAsArray[0].id"));
		assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("itensAsArray[0].productName"));
		assertEquals(order.getItens().get(1).getId(), orderGraph.get("itensAsArray[1].id"));
		assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("itensAsArray[1].productName"));
	}
	
	@Test(expected = ObjectGraphException.class)
	public void shouldNotAcceptCollectionDiferentFromListOrArray() {
		Order order = new Order(129, new Date(), 1650d);
		order.addItem(new Item(1, "MacBook pro 13"));
		order.addItem(new Item(2, "iPad Air 2"));
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itensAsSet")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertEquals(order.getItens().get(0).getId(), orderGraph.get("itensAsSet[0].id"));
		assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("itensAsSet[0].productName"));
		assertEquals(order.getItens().get(1).getId(), orderGraph.get("itensAsSet[1].id"));
		assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("itensAsSet[1].productName"));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldAccessInvalidCollectionItem() {
		Order order = new Order(129, new Date(), 1650d);
		order.setItens(new ArrayList<>());
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertNull(orderGraph.get("itens[0].productName"));
	}
	
	@Test
	public void shouldReturnNullIfAccessNullCollection() {
		Order order = new Order(129, new Date(), 1650d);
		order.setItens(null);
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertNull(orderGraph.get("itens[0].productName"));
	}
	
	@Test
	public void shouldGetDataFromCollectionInRootNode() {
		ObjectGraph graph = new ObjectGraphBuilder(Item.class)
				.build(Arrays.asList(new Item(1, "MacBook pro 13"),
						new Item(2, "iPad Air 2"),
						new Item(3, "iPad Mini")));
		assertTrue(graph.getRoot().isCollection());
		//when node is a collection, children are all collection items. 
		//each child node represents a collection item
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("$root[0].id"));
		assertNotNull(graph.getNode("$root[0].productName"));
		assertEquals(1, graph.get("$root[0].id"));
		assertEquals("MacBook pro 13", graph.get("$root[0].productName"));
		
		assertEquals(2, graph.get("$root[1].id"));
		assertEquals("iPad Air 2", graph.get("$root[1].productName"));
		
		assertEquals(3, graph.get("$root[2].id"));
		assertEquals("iPad Mini", graph.get("$root[2].productName"));
	}
	
	@Test
	public void shouldGetCollectionLengthInCollection() {
	    Order order = new Order(129, new Date(), 1650d);
	    order.addItem(new Item(1, "MacBook pro 13"));
	    order.addItem(new Item(2, "iPad Air 2"));
	    ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
	      .include("itens")
	      .build(order);
	    assertEquals(2, orderGraph.getCollectionLength("itens"));
	    assertEquals(order.getId(), orderGraph.get("id"));
	    assertEquals(order.getDate(), orderGraph.get("date"));
	    assertEquals(order.getAmount(), orderGraph.get("amount"));
	    assertEquals(order.getItens().get(0).getId(), orderGraph.get("itens[0].id"));
	    assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("itens[0].productName"));
	    assertEquals(order.getItens().get(1).getId(), orderGraph.get("itens[1].id"));
	    assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("itens[1].productName"));
	}
	
	@Test
	public void shouldGetCollectionLengthInArray() {
	    Order order = new Order(129, new Date(), 1650d);
	    order.addItem(new Item(1, "MacBook pro 13"));
	    order.addItem(new Item(2, "iPad Air 2"));
	    ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
	      .include("itensAsArray")
	      .build(order);
	    assertEquals(2, orderGraph.getCollectionLength("itensAsArray"));
	    assertEquals(order.getId(), orderGraph.get("id"));
	    assertEquals(order.getDate(), orderGraph.get("date"));
	    assertEquals(order.getAmount(), orderGraph.get("amount"));
	    assertEquals(order.getItens().get(0).getId(), orderGraph.get("itensAsArray[0].id"));
	    assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("itensAsArray[0].productName"));
	    assertEquals(order.getItens().get(1).getId(), orderGraph.get("itensAsArray[1].id"));
	    assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("itensAsArray[1].productName"));
	}
	
	@Test
	public void shouldGetCollectionLengthWhenCollectionIsRootNode() {
		ObjectGraph graph = new ObjectGraphBuilder(Item.class)
				.build(Arrays.asList(new Item(1, "MacBook pro 13"),
						new Item(2, "iPad Air 2"),
						new Item(3, "iPad Mini")));
		assertTrue(graph.getRoot().isCollection());
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("$root[0].id"));
		assertNotNull(graph.getNode("$root[0].productName"));
		
		assertEquals(3, graph.getCollectionLength("$root"));
		
		assertEquals(1, graph.get("$root[0].id"));
		assertEquals("MacBook pro 13", graph.get("$root[0].productName"));
		
		assertEquals(2, graph.get("$root[1].id"));
		assertEquals("iPad Air 2", graph.get("$root[1].productName"));
	}
	
	@Test
	public void shouldGetCollectionLengthInNullCollection() {
		Order order = new Order(129, new Date(), 1650d);
		order.setItens(null);
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("id"));
		assertEquals(order.getDate(), orderGraph.get("date"));
		assertEquals(order.getAmount(), orderGraph.get("amount"));
		assertEquals(0, orderGraph.getCollectionLength("itens"));
		assertNull(orderGraph.get("itens[0].productName"));
	}
	
	@Test
	public void shouldGetDataAccessByRootElementName() {
		Order order = new Order(129, new Date(), 1650d);
		order.setItens(null);
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(order);
		assertEquals(order.getId(), orderGraph.get("$root.id"));
		assertEquals(order.getDate(), orderGraph.get("$root.date"));
		assertEquals(order.getAmount(), orderGraph.get("$root.amount"));
		assertEquals(0, orderGraph.getCollectionLength("$root.itens"));
		assertNull(orderGraph.get("$root.itens[0].productName"));
	}
	
	@Test(expected = ObjectGraphException.class)
	public void shouldNotAlowGetUnmappedProperty() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class).build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
		assertEquals(john.getAddress(), johnGraph.get("address"));
	}
	
	@Test
	public void shouldReturnNullIfNullReferenceIsUsedInObjectGraph() {
		ObjectGraph nullGraph = new ObjectGraphBuilder(Person.class)
				.include("address")
				.build(null);
		assertNull(nullGraph.get("name"));
		assertNull(nullGraph.get("age"));
		assertNull(nullGraph.get("address"));
	}
	
	@Test
	public void shouldReturnNullIfNestedPropertyIsNull() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("address")
			.build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
		assertNull(johnGraph.get("address"));
		assertNull(johnGraph.get("address.zip"));
	}
	
	@Test
	public void shouldReturnNullIfNestedPropertyIsNullDeep() {
		Person john = new Person("John Smith", 30);
		john.setAddress(new Address(null, "29153040"));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("address")
			.include("address.city")
			.build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
		assertNotNull(johnGraph.get("address"));
		assertNotNull(johnGraph.get("address.zip"));
		assertNull(johnGraph.get("address.city.name"));
	}
	
	@Test(expected = ObjectGraphException.class)
	public void shouldNotAccessNonCollectionPropertyAsCollection() {
		Person john = new Person("John Smith", 30);
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("address")
			.build(john);
		assertEquals(john.getName(), johnGraph.get("name"));
		assertEquals(john.getAge(), johnGraph.get("age"));
		assertNotNull(johnGraph.get("address[0].city"));
	}
	
	@Test
	public void shouldSortAllChildrenByName() {
		Person john = new Person("John Smith", 30);
		john.setAddress(new Address(new City("New York", "NY"), "29153040"));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("address.city")
			.include("address")
			.build(john);
		johnGraph.sortByName();
		
		assertNotNull(johnGraph.getNode("name"));
		assertNotNull(johnGraph.getNode("age"));
		assertNotNull(johnGraph.getNode("address"));
		
		assertEquals(2, johnGraph.getNode("address").getChildren().size());
		assertNotNull(johnGraph.getNode("address.zip"));
		assertNotNull(johnGraph.getNode("address.city"));
		
		assertEquals(2, johnGraph.getNode("address.city").getChildren().size());
		assertNotNull(johnGraph.getNode("address.city.name"));
		assertNotNull(johnGraph.getNode("address.city.state"));
		
		List<Node> rootChildren = johnGraph.getNodes();
		assertEquals("address", rootChildren.get(0).getName());
		assertEquals("age", rootChildren.get(1).getName());
		assertEquals("name", rootChildren.get(2).getName());
		
		List<Node> addressChildren = johnGraph.getNode("address").getChildren();
		assertEquals("city", addressChildren.get(0).getName());
		assertEquals("zip", addressChildren.get(1).getName());
		
		List<Node> cityChildren = johnGraph.getNode("address.city").getChildren();
		assertEquals("name", cityChildren.get(0).getName());
		assertEquals("state", cityChildren.get(1).getName());
	}
	
	@Test
	public void shouldIncludePropertyByGenericCollection() {
		Order order = new Order(129, new Date(), 1650d);
		order.setCustomer(new Person("Alivim", 12));
	    order.addItem(new Item(1, "MacBook pro 13"));
	    order.addItem(new Item(2, "iPad Air 2"));
	    ObjectGraph graph = new ObjectGraphBuilder(Page.class)
	    	.include("content")
	    	.include("content.customer")
	    	.include("content.itens")
	    	.build(new Page<>(1, 1, Collections.singletonList(order)));
	    
	    assertEquals(3, graph.getNodes().size());
	    assertNotNull(graph.getNode("page"));
	    assertNotNull(graph.getNode("total"));
	    assertNotNull(graph.getNode("content"));
	    
	    //collection node have each collection item as node
	    assertTrue(graph.getNode("content").isCollection());
	    Node firstContentNode = graph.getNode("content[0]"); 
	    assertEquals(5, firstContentNode.getChildren().size());
	    assertNotNull(graph.getNode("content[0].id"));
	    assertNotNull(graph.getNode("content[0].customer"));
	    assertNotNull(graph.getNode("content[0].date"));
	    assertNotNull(graph.getNode("content[0].amount"));
	    assertNotNull(graph.getNode("content[0].itens"));
	    
	    assertEquals(2, graph.getNode("content[0].customer").getChildren().size());
	    assertNotNull(graph.getNode("content[0].customer.name"));
	    assertNotNull(graph.getNode("content[0].customer.age"));
	    
	    assertEquals(2, graph.getNode("content[0].itens").getChildren().size());
	    assertTrue(graph.getNode("content[0].itens").isCollection());
	    assertNotNull(graph.getNode("content[0].itens[0].id"));
	    assertNotNull(graph.getNode("content[0].itens[0].productName"));
	}
	
	@Test
	public void shouldIncludeGenericProperty() {
		Person person = new Person("James Tiberius Kirk", 90);
		person.setAddress(new Address(new City("Denver", "CO"), "29011"));
		WrapperData<Person> wrap = new WrapperData<>(person);
		ObjectGraph graph = new ObjectGraphBuilder(WrapperData.class)
			.include("data")
			.include("data.address")
			.include("data.address.city")
			.build(wrap);
		
		assertEquals(1, graph.getNodes().size());
		assertNotNull(graph.getNode("data"));
		
		assertEquals(3, graph.getNode("data").getChildren().size());
		assertNotNull(graph.getNode("data.name"));
		assertNotNull(graph.getNode("data.age"));
		assertNotNull(graph.getNode("data.address"));
		
		assertEquals(2, graph.getNode("data.address").getChildren().size());
		assertNotNull(graph.getNode("data.address.zip"));
		assertNotNull(graph.getNode("data.address.city"));
		
		assertEquals(2, graph.getNode("data.address.city").getChildren().size());
		assertNotNull(graph.getNode("data.address.city.name"));
		assertNotNull(graph.getNode("data.address.city.state"));
	}
	
	@Test
	public void shouldIgnoreDuplicateField() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.include("spouse")
			.include("spouse")
			.build(john);
		assertNotNull(johnGraph.getRoot());
		assertEquals(3, johnGraph.getNodes().size());
	}
	
	@Test
	public void shouldIgnoreDoubleExcludedField() {
		Person john = new Person("John Smith", 32, new Person("Joana Smith", 30));
		ObjectGraph johnGraph = new ObjectGraphBuilder(Person.class)
			.exclude("age")
			.exclude("age")
			.build(john);
		assertNotNull(johnGraph.getRoot());
		assertEquals(1, johnGraph.getNodes().size());
		assertNotNull(johnGraph.getNode("name"));
		assertNull(johnGraph.getNode("age"));
	}
	
	@Test
	public void shouldGetDataFromCollectionInNestedCollection() {
		Order order = new Order(129, new Date(), 1650d);
		order.addItem(new Item(1, "MacBook pro 13"));
		order.addItem(new Item(2, "iPad Air 2"));
		order.addItem(new Item(2, "iPad Mini"));
		ObjectGraph orderGraph = new ObjectGraphBuilder(Order.class)
			.include("itens")
			.build(Arrays.asList(order));
		assertTrue(orderGraph.getRoot().isCollection());
		assertEquals(4, orderGraph.getNode("$root[0]").getChildren().size());
		assertNotNull(orderGraph.getNode("$root[0].id"));
		assertNotNull(orderGraph.getNode("$root[0].date"));
		assertNotNull(orderGraph.getNode("$root[0].amount"));
		assertNotNull(orderGraph.getNode("$root[0].itens"));
		
		assertTrue(orderGraph.getNode("$root[0].itens").isCollection());
		assertEquals(3, orderGraph.getNode("$root[0].itens").getChildren().size());
		assertNotNull(orderGraph.getNode("$root[0].itens[0].id"));
		assertNotNull(orderGraph.getNode("$root[0].itens[0].productName"));
		
		assertEquals(order.getId(), orderGraph.get("$root[0].id"));
		assertEquals(order.getDate(), orderGraph.get("$root[0].date"));
		assertEquals(order.getAmount(), orderGraph.get("$root[0].amount"));
		assertEquals(order.getItens().get(0).getId(), orderGraph.get("$root[0].itens[0].id"));
		assertEquals(order.getItens().get(0).getProductName(), orderGraph.get("$root[0].itens[0].productName"));
		assertEquals(order.getItens().get(1).getId(), orderGraph.get("$root[0].itens[1].id"));
		assertEquals(order.getItens().get(1).getProductName(), orderGraph.get("$root[0].itens[1].productName"));
	}
	
	@Test
	public void shouldGetDataFromPolymorphismCollection() {
		Document doc = new Document("My document");
		doc.addEvent(new WordEvent(1000));
		doc.addEvent(new TableEvent(2));
		ObjectGraph docGraph = new ObjectGraphBuilder(Document.class)
				.include("events")
				.include("events.eventDetail")
				.build(doc);
		
		assertNotNull(docGraph.getNode("title"));
		assertNotNull(docGraph.getNode("creationDate"));
		
		assertTrue(docGraph.getNode("events").isCollection());
		
		assertEquals(4, docGraph.getNode("events[0]").getChildren().size());
		assertNotNull(docGraph.getNode("events[0].type"));
		assertNotNull(docGraph.getNode("events[0].date"));
		assertNotNull(docGraph.getNode("events[0].descriptionWordEvent"));
		assertNotNull(docGraph.getNode("events[0].eventDetail"));
		assertEquals(2, docGraph.getNode("events[0].eventDetail").getChildren().size());
		assertNotNull(docGraph.getNode("events[0].eventDetail.description"));
		assertNotNull(docGraph.getNode("events[0].eventDetail.numberOfNewWords"));
		
		assertEquals(4, docGraph.getNode("events[1]").getChildren().size());
		assertNotNull(docGraph.getNode("events[1].type"));
		assertNotNull(docGraph.getNode("events[1].date"));
		assertNotNull(docGraph.getNode("events[1].descriptionTableEvent"));
		assertNotNull(docGraph.getNode("events[1].eventDetail"));
		assertEquals(2, docGraph.getNode("events[1].eventDetail").getChildren().size());
		assertNotNull(docGraph.getNode("events[1].eventDetail.description"));
		assertNotNull(docGraph.getNode("events[1].eventDetail.numberOfNewCells"));
		
	}
	
}
