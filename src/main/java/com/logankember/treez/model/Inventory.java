package com.logankember.treez.model;

import java.io.Serializable;

public class Inventory implements Serializable{
	public Integer id;
	public String name;
	public String description;
	public Double price;
	public Integer numAvailable;
	
	public Inventory(int id, String name, String description, double price, int quantity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.numAvailable = quantity;
	}
}
