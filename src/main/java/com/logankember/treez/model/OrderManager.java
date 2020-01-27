package com.logankember.treez.model;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import com.logankember.treez.sqlite.SQLManager;

public class OrderManager {
	
	public OrderManager() {
	}
	
	public List<Order> getAllOrders() {
		try {
			return SQLManager.getAllOrders();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	public Order getOrder(int id) {
		try {
			return SQLManager.getOrderWithID(id);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	public String addNewOrder(@Valid Order order) {
		try {
			if (SQLManager.createNewOrder(order)) {
				return "Success";
			}
			return "Order with that ID already exists";
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			String output = "Error: " + e.getMessage() + "\n";
			StackTraceElement[] s = e.getStackTrace();
			for (int i = 0; i < s.length; i++) {
				output += "\n\t" + s[i].toString();
			}
			return output;
		}
	}

	public String updateOrder(int id, Order order) {
		try {
			if (SQLManager.updateOrderItem(id, order)) {
				return "Success";
			}
			else {
				return "Couldn't find order with that ID";
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return "Error: " + e.getMessage();
		}
	}

	public String removeOrder(int id) {
		try {
			SQLManager.removeOrder(id);
			return "Success";
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			return "Error: " + e.getMessage();
		}
	}
}
