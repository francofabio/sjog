package br.com.binarti.sjog.model;

public class Address {
	private City city;
	private String zip;

	public Address(City city, String zip) {
		super();
		this.city = city;
		this.zip = zip;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

}
