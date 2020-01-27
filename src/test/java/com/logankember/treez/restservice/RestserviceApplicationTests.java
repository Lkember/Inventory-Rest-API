package com.logankember.treez.restservice;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.logankember.treez.enums.OrderStatus;
import com.logankember.treez.model.Inventory;
import com.logankember.treez.model.Order;
import com.logankember.treez.model.OrderInventory;
import com.logankember.treez.sqlite.SQLManager;

@SpringBootTest
class InventoryTest {
	
	// INVENTORY
	
	@Test
	void addingInventoryAndVerify() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			assertTrue(SQLManager.insertInventory(item));
			assertTrue(SQLManager.getInventoryWithID(item.id) != null);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void updatingInventoryItem() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			Inventory newItem = new Inventory(12345, "Inventory Item updated", "Test inventory item", 9.98, 18);
			
			assertTrue(SQLManager.insertInventory(item));
			assertTrue(SQLManager.updateInventoryItem(newItem));
			
			Inventory tempItem = SQLManager.getInventoryWithID(newItem.id);
			
			assertTrue(newItem.id.equals(tempItem.id), newItem.id + " vs " + tempItem.id);
			assertTrue(newItem.name.equals(tempItem.name));
			assertTrue(newItem.description.equals(tempItem.description)); 
			assertTrue(newItem.price.equals(tempItem.price));
			assertTrue(newItem.numAvailable.equals(tempItem.numAvailable));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	
	@Test
	void updatingInventoryItemWithInvalidQuantity() {
		try {
			SQLManager.closeConnection();
			
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			Inventory newItem = new Inventory(12345, "Inventory Item updated", "Test inventory item", 9.98, -2);
			
			assertTrue(SQLManager.insertInventory(item));
			
			try {
				boolean test = SQLManager.updateInventoryItem(newItem);
				assertFalse(test);
				fail("Error: Inventory update should have failed");
			}
			catch (Exception e) {
				// nothing to do
			}
			
			Inventory tempItem = SQLManager.getInventoryWithID(newItem.id);
			
			assertTrue(item.id.equals(tempItem.id), item.id + " vs " + tempItem.id);
			assertTrue(item.name.equals(item.name));
			assertTrue(item.description.equals(tempItem.description)); 
			assertTrue(item.price.equals(tempItem.price));
			assertTrue(item.numAvailable.equals(tempItem.numAvailable));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	
	// When removing inventory, I decided to just set the quantity to 0 since there may be some references to it on different orders
	@Test 
	void removingInventoryItem() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			assertTrue(SQLManager.insertInventory(item));
			assertTrue(SQLManager.getInventoryWithID(item.id) != null);
			
			SQLManager.removeInventoryItem(item.id);
			
			Inventory newItem = SQLManager.getInventoryWithID(item.id);
			assertTrue(newItem.numAvailable == 0);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test 
	void readAllInventoryItems() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			HashMap<Integer, Inventory> map = new HashMap<Integer, Inventory>();
			for (int i = 1; i < 11; i++) {
				Inventory temp = new Inventory(i, "Inventory Item " + i, "item " + i, i+0.99, 10);
				assertTrue(SQLManager.insertInventory(temp));
				map.put(temp.id, temp);
			}
			
			List<Inventory> allInv = SQLManager.getAllInventory();
			
			for (int i = 0; i < 10; i++) {
				Inventory item = allInv.get(i);
				Inventory temp = map.get(item.id);
				
				assertTrue(item.id.equals(temp.id), item.id + " vs " + temp.id);
				assertTrue(item.name.equals(temp.name));
				assertTrue(item.description.equals(temp.description)); 
				assertTrue(item.price.equals(temp.price));
				assertTrue(item.numAvailable.equals(temp.numAvailable));
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void createNewOrder() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			assertTrue(SQLManager.insertInventory(item));
			
			Order order = new Order(1, "test@email.com");
			Inventory inv = SQLManager.getInventoryWithID(12345);
			OrderInventory invOrder = new OrderInventory(inv.id, 1, inv.name, inv.price);
			
			order.inventory.add(invOrder);
			assertTrue(SQLManager.createNewOrder(order));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void getOrderWithID() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			SQLManager.insertInventory(item);
			
			Order oldOrder = new Order(1, "test@email.com");
			Inventory inv = SQLManager.getInventoryWithID(12345);
			OrderInventory invOrder = new OrderInventory(inv.id, 1, inv.name, inv.price);
			
			oldOrder.inventory.add(invOrder);
			SQLManager.createNewOrder(oldOrder);
			
			Order newOrder = SQLManager.getOrderWithID(oldOrder.orderID);
			
			assertTrue(newOrder.orderID == oldOrder.orderID);
			assertTrue(newOrder.customerEmail.equals(oldOrder.customerEmail));
			assertTrue(newOrder.orderStatus.equals(oldOrder.orderStatus));
			
			assertTrue(newOrder.inventory.get(0).inventoryID == oldOrder.inventory.get(0).inventoryID);
			assertTrue(newOrder.inventory.get(0).cost == oldOrder.inventory.get(0).cost);
			assertTrue(newOrder.inventory.get(0).quantity == oldOrder.inventory.get(0).quantity);
			assertTrue(newOrder.inventory.get(0).name.equals(oldOrder.inventory.get(0).name));
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void getOrderWithInvalidID() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			SQLManager.insertInventory(item);
			
			Order oldOrder = new Order(1, "test@email.com");
			Inventory inv = SQLManager.getInventoryWithID(12345);
			OrderInventory invOrder = new OrderInventory(inv.id, 1, inv.name, inv.price);
			
			oldOrder.inventory.add(invOrder);
			SQLManager.createNewOrder(oldOrder);
			
			Order newOrder = SQLManager.getOrderWithID(oldOrder.orderID+1);
			if (newOrder != null) {
				fail();
			}
			
		}
		catch(Exception e) {
			fail();
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void getAllOrders() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			HashMap<Integer, Inventory> map = new HashMap<Integer, Inventory>();
			for (int i = 1; i < 11; i++) {
				Inventory temp = new Inventory(i, "Inventory Item " + i, "item " + i, i+0.99, 10);
				assertTrue(SQLManager.insertInventory(temp));
				map.put(temp.id, temp);
			}
			
			for (int i = 1; i < 11; i++) {
				Order order = new Order(i, "test" + i + "@email.com");
				Inventory inv = SQLManager.getInventoryWithID(i);
				OrderInventory invOrder = new OrderInventory(inv.id, i, inv.name, inv.price);
				order.inventory.add(invOrder);
				
				SQLManager.createNewOrder(order);
			}
			
			for (int i = 1; i < 11; i++) {
				Order newOrder = SQLManager.getOrderWithID(i);
				
				assertTrue(newOrder.orderID == i);
				assertTrue(newOrder.customerEmail.equals("test" + i + "@email.com"));
				assertTrue(newOrder.orderStatus.equals(OrderStatus.PendingConfirmation));
				
				assertTrue(newOrder.inventory.get(0).inventoryID == i);
				assertTrue(newOrder.inventory.get(0).cost == i+0.99);
				assertTrue(newOrder.inventory.get(0).quantity == i);
				assertTrue(newOrder.inventory.get(0).name.equals("Inventory Item " + i));
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void quantityChangeOnInventory() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			HashMap<Integer, Inventory> map = new HashMap<Integer, Inventory>();
			for (int i = 1; i < 11; i++) {
				Inventory temp = new Inventory(i, "Inventory Item " + i, "item " + i, i+0.99, 10);
				assertTrue(SQLManager.insertInventory(temp));
				map.put(temp.id, temp);
			}
			
			for (int i = 1; i < 11; i++) {
				Order order = new Order(i, "test" + i + "@email.com");
				Inventory inv = SQLManager.getInventoryWithID(i);
				OrderInventory invOrder = new OrderInventory(inv.id, i, inv.name, inv.price);
				order.inventory.add(invOrder);
				
				SQLManager.createNewOrder(order);
			}
			
			for (int i = 1; i < 11; i++) {
				Inventory inv  = SQLManager.getInventoryWithID(i);
				
				assertTrue(inv.numAvailable == 10 - i);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	
	@Test void updateOrderInformation() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			SQLManager.insertInventory(item);
			
			item = new Inventory(1, "another", "description", 10.99, 13);
			SQLManager.insertInventory(item);
			
			Order oldOrder = new Order(1, "test@email.com");
			Inventory inv = SQLManager.getInventoryWithID(12345);
			OrderInventory invOrder = new OrderInventory(inv.id, 1, inv.name, inv.price);
			
			oldOrder.inventory.add(invOrder);
			SQLManager.createNewOrder(oldOrder);
			
			Order newOrder = SQLManager.getOrderWithID(oldOrder.orderID);
			newOrder.customerEmail = "new@email.com";
			newOrder.orderStatus = OrderStatus.Confirmed;
			OrderInventory temp = newOrder.inventory.get(0);
			invOrder = new OrderInventory(temp.inventoryID, 0, temp.name, temp.cost);
			invOrder.quantity = 0;
			newOrder.inventory.clear();
			newOrder.inventory.add(invOrder);
			
			inv = SQLManager.getInventoryWithID(1);
			invOrder = new OrderInventory(inv.id, 5, inv.name, inv.price);
			newOrder.inventory.add(invOrder);
			
			SQLManager.updateOrderItem(newOrder.orderID, newOrder);
			Order tempOrder = SQLManager.getOrderWithID(newOrder.orderID);
			
			assertTrue(newOrder.orderID == tempOrder.orderID);
			assertTrue(newOrder.customerEmail.equals(tempOrder.customerEmail));
			assertTrue(newOrder.orderStatus.equals(tempOrder.orderStatus), newOrder.orderStatus.toString() + " != " + tempOrder.orderStatus.toString());
			
			assertTrue(newOrder.inventory.get(newOrder.inventory.size() - 1).inventoryID == invOrder.inventoryID);
			assertTrue(newOrder.inventory.get(newOrder.inventory.size() - 1).cost == invOrder.cost);
			assertTrue(newOrder.inventory.get(newOrder.inventory.size() - 1).quantity == invOrder.quantity);
			assertTrue(newOrder.inventory.get(newOrder.inventory.size() - 1).name.equals(invOrder.name));
		}
		catch(Exception e) {
			String str = "";
			str += "Error: ";
			
			for (int i = 0; i < e.getStackTrace().length; i++) {
				str += "\n" + e.getStackTrace()[i].toString();
			}
			fail("Error: " + e.getMessage() + "\n" + str);
		}
		
		SQLManager.closeConnection();
	}
	
	@Test
	void deleteExistingOrder() {
		try {
			SQLManager.closeConnection();
			SQLManager.initializeDatabase();
			
			Inventory item = new Inventory(12345, "Inventory Item", "This is a test inventory item", 9.99, 20);
			assertTrue(SQLManager.insertInventory(item));
			
			Order order = new Order(1, "test@email.com");
			Inventory inv = SQLManager.getInventoryWithID(12345);
			OrderInventory invOrder = new OrderInventory(inv.id, 1, inv.name, inv.price);
			
			order.inventory.add(invOrder);
			SQLManager.createNewOrder(order);
			
			assertTrue(SQLManager.removeOrder(1));
			assertTrue(SQLManager.getOrderWithID(1) == null);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			fail("Error: " + e.getMessage());
		}
		
		SQLManager.closeConnection();
	}
	
	public String getStuff(List<OrderInventory> orders) {
		String s = "";
		for (int i = 0; i < orders.size(); i++) {
			OrderInventory o = orders.get(i);
			s += "{ " + o.inventoryID + ", " + o.name + ", " + o.quantity + " }\n";
		}
		
		return s;
	}
}
