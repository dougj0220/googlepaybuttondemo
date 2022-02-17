package com.bhn.gpay.domain;

public class Product {
	
	public String name;
	public String imgLink;
	public double price;

	public Product(String name)  {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setImage(String img) {
		imgLink = img;
	}
	
	public String getImage() {
		return imgLink;
	}
	
	public void setPrice(double price) {
		this.price = price;
	} 
	
	public double getPrice() {
		return price;
	}
}
