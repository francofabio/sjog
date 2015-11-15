package br.com.binarti.sjog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Order {

	private int id;
	private Person customer;
	private Date date;
	private double amount;
	private List<Item> itens;

	public Order(int id, Date date, double amount) {
		this.id = id;
		this.date = date;
		this.amount = amount;
		this.itens = new ArrayList<>();
	}
	
	public int getId() {
		return id;
	}
	
	public Person getCustomer() {
		return customer;
	}
	
	public void setCustomer(Person customer) {
		this.customer = customer;
	}
	
	public Date getDate() {
		return date;
	}
	
	public double getAmount() {
		return amount;
	}

	public List<Item> getItens() {
		return itens;
	}
	
	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
	public void addItem(Item item) {
		itens.add(item);
	}
	
	public Item[] getItensAsArray() {
		return itens.toArray(new Item[0]);
	}
	
	public Set<Item> getItensAsSet() {
		return new HashSet<>(itens);
	}

}
