package br.com.binarti.sjog.model;

public class Item {

	private int id;
	private String productName;

	public Item(int id, String productName) {
		this.id = id;
		this.productName = productName;
	}
	
	public int getId() {
		return id;
	}
	
	public String getProductName() {
		return productName;
	}

}
