package com.logankember.treez.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logankember.treez.enums.OrderStatus;

public class Order implements Serializable {
	public int orderID;
	public String customerEmail;
	public Date orderPlaced;
	public OrderStatus orderStatus;
	public List<OrderInventory> inventory;
	
	public Order() {
	}
	
	public Order(int orderID, String email) {
		this.orderID = orderID;
		this.orderPlaced = new Date();
		this.customerEmail = email;
		this.orderStatus = OrderStatus.PendingConfirmation;
		this.inventory = new ArrayList<OrderInventory>();
	}
	
	public Order(int orderID, String email, Date date) {
		this.orderID = orderID;
		this.orderPlaced = date;
		this.customerEmail = email;
		this.orderStatus = OrderStatus.PendingConfirmation;
		this.inventory = new ArrayList<OrderInventory>();
	}
	
	public Order(int orderID, String customerEmail, Date orderPlaced, String orderStatus, List<OrderInventory> inventory) {
		this.orderID = orderID;
		this.customerEmail = customerEmail;
		this.orderPlaced = orderPlaced;
		this.orderStatus = OrderStatus.valueOf(orderStatus);
		if (inventory != null) {
			this.inventory = inventory;
		}
		else {
			this.inventory = new ArrayList<OrderInventory>();
		}
	}
}
