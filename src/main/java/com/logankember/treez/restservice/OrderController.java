package com.logankember.treez.restservice;

import java.util.List;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logankember.treez.model.*;
import com.logankember.treez.sqlite.SQLManager;

@RestController
public class OrderController {
	
	public InventoryManager inventoryManager;
	public OrderManager orderManager;
	
	public OrderController() throws Exception {
		inventoryManager = new InventoryManager();
		orderManager = new OrderManager();
		
		SQLManager.initializeDatabase();
	}
	
	
	// INVENTORY
	@GetMapping("/inventories")
	public List<Inventory> getAllInventory() {
		return inventoryManager.getAllInventory();
	}
	
	@GetMapping("/inventories/{id}")
	public Inventory getInventoryWithId(@PathVariable(value = "id") int id) {
		return inventoryManager.getInventory(id);
	}
	
	@PostMapping("/inventories")
	public String addNewInventoryItem(@Valid @RequestBody Inventory inventory) {
		return inventoryManager.addNewInventoryItem(inventory);
	}
	
	@PutMapping("/inventories/{id}")
	public String updateInventory(@PathVariable(value = "id") int id, @RequestBody Inventory newInventory) {
		Inventory inventory = inventoryManager.getInventory(id);
		
		if (newInventory.description != null && !newInventory.description.equals("")) {
			inventory.description = newInventory.description;
		}
		
		if (newInventory.name != null && !newInventory.name.equals("")) {
			inventory.name = newInventory.name;
		}
		
		if (newInventory.price != null) { 
			inventory.name = newInventory.name;
		}
		
		if (newInventory.numAvailable != null && !newInventory.name.equals("")) {
			inventory.name = newInventory.name;
		}
		
		return inventoryManager.updateInventory(id, inventory);
	}
	
	@DeleteMapping("/inventories/{id}")
	public String removeInventory(@PathVariable(value = "id") int id) {
		return inventoryManager.removeInventory(id);
	}
	
	// ORDERS
	
	@GetMapping("/orders")
	public List<Order> getAllOrders() {
		return orderManager.getAllOrders();
	}
	
	@GetMapping("/orders/{id}")
	public Order getOrderWithId(@PathVariable(value = "id") int id) {
		return orderManager.getOrder(id);
	}
	
	@PostMapping("/orders")
	public String addNewOrder(@Valid @RequestBody Order order) {
		return orderManager.addNewOrder(order);
	}
	
	@PutMapping("/orders/{id}")
	public String updateOrder(@PathVariable(value = "id") int id, @RequestBody Order order) {
		Order newOrder = orderManager.getOrder(id);
		
		if (order.customerEmail != null && !order.customerEmail.equals("")) {
			newOrder.customerEmail = order.customerEmail;
		}
		
		if (order.orderPlaced != null) {
			newOrder.orderPlaced = order.orderPlaced;
		}
		
		if (order.orderStatus != null) { 
			newOrder.orderStatus = order.orderStatus;
		}
		
		if (order.inventory != null) {
			newOrder.inventory = order.inventory;
		}
		
		return orderManager.updateOrder(id, order);
	}
	
	@DeleteMapping("/orders/{id}")
	public String removeOrder(@PathVariable(value = "id") int id) {
		return orderManager.removeOrder(id);
	}
}
