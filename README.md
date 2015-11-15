# Simple Java Object-Graph
This project is intended to help developers who need to dynamically access properties in Java objects through simple expressions based on string.

## How to use
To use this library, you need install the package in your maven local repository:
```
mvn install
```

And add this dependency in your pom.xml

```xml
<dependency>
    <groupId>br.com.binarti</groupId>
    <artifactId>sjog</artifactId>
    <version>1.0</version> <!-- or the last version -->
</dependency>
```

This library is not available yet in maven central repository, it is coming son.<br/>
If you not use maven. You can also build the jar and add in classpath of the Java app.<br/>
To build jar package, you need install maven and execute the command:
```
mvn clean package
```
The jar package is available in target/ directory.


## Quick start
The 'default object graph predicate' automatically include all properties of the types in object graph: <br/>
byte, short, int, long, float, double, boolean, char, java.lang.String, java.lang.Enum, java.util.Date, java.util.Calendar
```java
import br.com.binarti.sjog.ObjectGraphBuilder;
...
private static class Person {
    private String name;
    private int age;
    //getters and setters omitted
}

ObjectGraph objectGraph = new ObjectGraphBuilder(Person.class)
        .build(new Person("John Galt", 43);
String name = (String) objectGraph.get("name"); //get name property value by name
Integer age = (Integer) objectGraph.get("age"); //get age property value by name
```
In the above example, an object graph was created to enable access the properties of the person object by the name.

#### Nested properties
```java
import br.com.binarti.sjog.ObjectGraphBuilder;
...

private static class Order {
    private int id;
  private Person customer;
  private Date date;
  private double amount;
  private List<Item> itens;
  //constructors, getters and setters are omitted
}
private static class Item {
  private int id;
  private String productName;
  //constructors, getters and setters are omitted
}
Order order = new Order(129, new Person("Kane"), new Date(), 1650d);
order.addItem(new Item(1, "MacBook pro 13"));
order.addItem(new Item(2, "iPad Air 2"));
ObjectGraph orderObjectGraph = new ObjectGraphBuilder(Order.class)
        .include("customer") //include all properties of the java basic type in customer
        .include("itens") //include all properties of the java basic type in itens (considering generic type of the collection or the collection content
        .build(order);
Integer orderId = orderObjectGraph.get("id"); //get id property of the order
String customerName = orderObjectGraph.get("customer.name"); //get the name of the customer in order
String firstProductName = orderObjectGraph.get("itens[0].productName"); //get the product name of the first item in collection itens
```