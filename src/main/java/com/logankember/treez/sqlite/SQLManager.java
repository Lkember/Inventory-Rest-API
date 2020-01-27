package com.logankember.treez.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.logankember.treez.model.*;

public class SQLManager {
	
	public static Connection connection;
//	public static Statement statement;
	
	// --- Inventory ---
	
	public static boolean insertInventory(Inventory item) throws SQLException {
		Statement statement = connection.createStatement();
		
		if (SQLManager.getInventoryWithID(item.id) == null) {
			statement.executeUpdate("insert into inventory (itemID, name, description, price, numAvailable)"
					+ " values (" + item.id.toString() + ", '" + item.name + "', '" + item.description + "', '"
					+ item.price.toString() + "', " + item.numAvailable + ")");
			return true;				
		}
		else {
			return false;
		}
	}
	
	public static List<Inventory> getAllInventory() throws SQLException {
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery("select * from inventory");
		List<Inventory> output = new ArrayList<Inventory>();
  
		while(rs.next())
		{
			Inventory inv = new Inventory(rs.getInt("itemID"),rs.getString("name"), rs.getString("description"), rs.getDouble("price"), rs.getInt("numAvailable"));
			output.add(inv);
		}
		
		return output;
	}
	
	public static Inventory getInventoryWithID(int id) throws SQLException {
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery("select * from inventory where itemID = " + id);
  
		if (rs.next())
		{
			return new Inventory(rs.getInt("itemID"),rs.getString("name"), rs.getString("description"), rs.getDouble("price"), rs.getInt("numAvailable"));
		}
		
		return null;
	}
	
	public static boolean updateInventoryItem(Inventory item) throws Exception {
		Statement statement = connection.createStatement();
		
		if (SQLManager.getInventoryWithID(item.id) == null) {
			return false;
		}
		else if (item.numAvailable < 0) {
			throw new Exception("Invalid number of units available");
		}
		
		statement.executeUpdate("update inventory set name = '" + item.name + "', description = '" + item.description 
				+ "', price = '" + item.price.toString() + "', numAvailable = '" + item.numAvailable.toString() + "' where itemID = " + item.id.toString());
		return true;
	}
	
	// Rather than deleting the item from the table we will set the quantity to 0, 
	// as there may be orders referencing this inventory item
	public static boolean removeInventoryItem(int id) throws SQLException {
		Statement statement = connection.createStatement();
		
		if (SQLManager.getInventoryWithID(id) == null) {
			return false;
		}
		
		statement.executeUpdate("update inventory set numAvailable = 0 where itemID = " + id);
		return true;
	}
	
	
	// --- Orders ---
	
	private static List<Inventory> checkOrderQuantities(Order order) throws Exception {
		List<Inventory> allInventory = new ArrayList<Inventory>();
		
		for (int i = 0; i < order.inventory.size(); i++) {
			OrderInventory item = order.inventory.get(i);
			
			Inventory inv = SQLManager.getInventoryWithID(item.inventoryID);
			
			allInventory.add(inv);
			
			if (inv == null) {
				throw new Exception("Invalid inventory item with ID: " + item.inventoryID);
			}
			else if (item.quantity <= 0 || item.quantity > inv.numAvailable) {
				throw new Exception("Invalid quantity amount for item with ID: " + item.inventoryID);
			}
		}
		
		return allInventory;
	}
	
	public static boolean createNewOrder(Order order) throws Exception {
		Statement statement = connection.createStatement();
		
		if (SQLManager.getOrderWithID(order.orderID) != null) {
			return false;
		}
			
		if (order.inventory == null || order.inventory.size() == 0) {
			throw new Exception("The order was empty");
		}
		List<Inventory> allInventory = checkOrderQuantities(order);
		
		statement.executeUpdate("insert into orders (orderID, email, orderPlaced, orderStatus) "
				+ "values (" + order.orderID + ", '" + order.customerEmail + "', '" + order.orderPlaced.toString() + "', '" + order.orderStatus + "')");
		
		for (int i = 0; i < order.inventory.size(); i++) {
			OrderInventory item = order.inventory.get(i);
			Inventory inv = allInventory.get(i);
			
			statement.executeUpdate("insert into orderItems (orderID, inventoryID, quantity, price) "
					+ "values (" + order.orderID + ", " + item.inventoryID + ", " + item.quantity + ", " + item.cost + ")");
			
			inv.numAvailable -= item.quantity;
			SQLManager.updateInventoryItem(inv);
		}
		
		return true;
	}
	
	public static List<Order> getAllOrders() throws Exception {
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery("select orders.orderID, email, inventoryID, orderPlaced, orderStatus, quantity, price from orders "
				+ "left join orderItems on orders.orderID = orderItems.orderID ");
		HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
		
		while (rs.next()) {
			int id = rs.getInt("orderID");
			
			if (!orders.containsKey(id)) {
				Order order = new Order(id, rs.getString("email"));
				orders.put(id, order);
			}
			
			Order order = orders.get(id);
			int inventoryID = rs.getInt("inventoryID");
			
			Inventory inventory = SQLManager.getInventoryWithID(inventoryID);
			if (inventory == null) {
				throw new Exception("Unable to find the requested inventory with ID = " + inventoryID);
			}
			
			order.inventory.add(new OrderInventory(inventoryID, rs.getInt("quantity"), inventory.name, rs.getDouble("price")));
		}
		
		return new ArrayList<Order>(orders.values());
	}
	
	public static Order getOrderWithID(int id) throws Exception {
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery("select orders.orderID, email, orderPlaced, orderStatus, inventoryID, quantity, name, orderItems.price from orders "
				+ "left join orderItems on orders.orderID = orderItems.orderID "
				+ "inner join inventory on orderItems.inventoryID = inventory.itemID "
				+ "where orders.orderID = " + id);
		Order output = null;
		
		while (rs.next()) {
			if (output == null) {
				List<OrderInventory> inv = null;
				output = new Order(rs.getInt("orderID"), rs.getString("email"), rs.getDate("orderPlaced"), rs.getString("orderStatus"), inv);
			}
			
			int inventoryID = rs.getInt("inventoryID");
			Inventory inventory = SQLManager.getInventoryWithID(inventoryID);
			if (inventory == null) {
				throw new Exception("Unable to find the requested inventory with ID = " + inventoryID);
			}
			
			output.inventory.add(new OrderInventory(inventoryID, rs.getInt("quantity"), rs.getString("name"), rs.getDouble("price")));
		}
		
		return output;
	}
	

	public static boolean updateOrderItem(int id, Order order) throws Exception {
		Statement statement = connection.createStatement();
		
		Order oldOrder = SQLManager.getOrderWithID(order.orderID);
		if (oldOrder == null) {
			return false;
		}
		
		if (order.inventory == null || order.inventory.size() == 0) {
			throw new Exception("Invalid inventory");
		}
		
		HashMap<Integer, OrderInventory> orderItems = new HashMap<Integer, OrderInventory>();
		for (int i = 0; i < oldOrder.inventory.size(); i++) {
			OrderInventory curr = oldOrder.inventory.get(i);
			orderItems.put(curr.inventoryID, curr);
		}
		
		if (!checkOrderQuantitiesWithOldOrder(oldOrder, orderItems)) {
			throw new Exception("Invalid quantities received");
		}
		
		statement.executeUpdate("update orders set orderID = " + order.orderID + ", email = '" + order.customerEmail + "', orderPlaced = '" + order.orderPlaced + "', orderStatus = '" + order.orderStatus 
				+ "' where orderID = " + id);
		
		for (int i = 0; i < order.inventory.size(); i++) {
			OrderInventory currOrder = order.inventory.get(i);
			
			if (orderItems.containsKey(currOrder.inventoryID)) {
				if (currOrder.quantity > 0) {
					statement.executeUpdate("update orderItems set quantity = " + currOrder.quantity + " where inventoryID = " + currOrder.inventoryID);
				}
				else {
					statement.executeUpdate("delete from orderItems where orderID = " + order.orderID + " and inventoryID = " + currOrder.inventoryID);
				}
			}
			else {
				if (SQLManager.getInventoryWithID(currOrder.inventoryID) != null) {
					statement.executeUpdate("insert into orderItems (orderID, inventoryID, quantity, price) "
						+ "values (" + id + ", " + currOrder.inventoryID + ", " + currOrder.quantity + ", " + currOrder.cost + ")");
				}
				else {
					throw new Exception("Inventory with that ID doesn't exist");
				}
			}
		}
		
		return true;
	}
	
	public static boolean checkOrderQuantitiesWithOldOrder(Order oldOrder, HashMap<Integer, OrderInventory> items) throws Exception {
		for (int i = 0; i < oldOrder.inventory.size(); i++) {
			OrderInventory oldItem = oldOrder.inventory.get(i);
			OrderInventory newItem = items.get(oldItem.inventoryID);
			
			if (newItem == null) {
				throw new Exception("Could not find inventory item with ID: " + oldItem.inventoryID);
			}
			
			Inventory currItem = SQLManager.getInventoryWithID(oldItem.inventoryID);
			int quantityDiff = oldItem.quantity - newItem.quantity;
			
			if (currItem.numAvailable + quantityDiff < 0 || newItem.quantity < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean removeOrder(int id) throws Exception {
		Statement statement = connection.createStatement();
		
		if (SQLManager.getOrderWithID(id) == null) {
			return false;
		}
		
		statement.executeUpdate("delete from orders where orderID = " + id);
		return true;
	}
	
	
	// --- Database initialization ---
	
	// Initializes the database tables
	// Note that this would not be necessary if we created a local file acting as a database
	// but instead to keep things simple, the DB is just being stored in memory and is not persistent
	public static void initializeDatabase() throws Exception
	{
	    // load the sqlite-JDBC driver using the current class loader
	    Class.forName("org.sqlite.JDBC");
	    Statement statement;
	    
	    try
	    {
	    		// create a database connection
	    		SQLManager.openConnection();
			statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate("drop table if exists orders");
			statement.executeUpdate("create table orders (orderID integer PRIMARY KEY, email string, orderPlaced date, orderStatus string)");
			statement.executeUpdate("drop table if exists orderItems");
			statement.executeUpdate("create table orderItems (orderID integer, inventoryID integer, quantity integer, price double)");
			statement.executeUpdate("drop table if exists inventory");
			statement.executeUpdate("create table inventory (itemID integer PRIMARY KEY, name string NOT NULL, description string, price double, numAvailable integer)");
			
//			Inventory inv = new Inventory(1, "test inventory", "this is a test", 5.0, 20);
//			SQLManager.insertInventory(inv);
			  
			//	      statement.executeUpdate("insert into inventory (itemID, name, description, price, quantity) "
			//	      		+ "values(1, 'test inventory', 'this is a test', '5.00', 20)");
			  
//			Order order = new Order(1, "test@email.com");
//			OrderInventory oInv = new OrderInventory(inv.id, 2, inv.name, 23.99);
//			  
//			order.inventory.add(oInv);
//			SQLManager.createNewOrder(order);
	    }
	    catch(Exception e)
	    {
	        System.err.println(e.getMessage());
	    }
	}
	
	public static void closeConnection() {
		try {
			if (!connection.isClosed()) {
				connection.close();
			}
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void openConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:sqlite::memory");
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
