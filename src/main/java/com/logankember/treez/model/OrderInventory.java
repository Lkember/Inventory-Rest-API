package com.logankember.treez.model;

public class OrderInventory {
	public int inventoryID;
	public int quantity;
	public String name;
	public double cost;
	
	public OrderInventory(int id, int quantity, String name, double cost) {
		this.inventoryID = id;
		this.quantity = quantity;
		this.name = name;
		this.cost = cost;
	}
}
