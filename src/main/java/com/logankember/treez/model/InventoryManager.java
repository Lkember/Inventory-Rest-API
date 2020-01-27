package com.logankember.treez.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Component;

import com.logankember.treez.sqlite.SQLManager;

@Component("inventoryManager")
public class InventoryManager {
	public InventoryManager() {
	}
	
	public boolean addInventory(Inventory item) throws SQLException {
		try {
			return SQLManager.insertInventory(item);
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	public List<Inventory> getAllInventory() {
		try {
			return SQLManager.getAllInventory();
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return new ArrayList<Inventory>();
		}
	}

	public Inventory getInventory(int id) {
		try {
			return SQLManager.getInventoryWithID(id);
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	public String addNewInventoryItem(@Valid Inventory item) {
		try {
			if (SQLManager.insertInventory(item)) {
				return "Successfully added";
			}
			
			return "Failure: Item with that ID already exists";
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return "SQL command failed: " + e.getMessage();
		}
	}

	public String updateInventory(int id, Inventory item) {
		try {
			boolean result = SQLManager.updateInventoryItem(item);
			
			if (result) {
				return "Successfully updated item";
			}
			return "Error: Could not find item with that ID";
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return "Error: " + e.getMessage();
		}
	}

	public String removeInventory(int id) {
		try {
			if (SQLManager.removeInventoryItem(id)) {
				return "Successfully removed item";
			}
			return "Error: Could not find item with that ID";
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			return "SQL command failed: " + e.getMessage();
		}
	}
	
}
