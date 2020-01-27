# Inventory-Rest-API

A simple service that acts as a RestAPI for an inventory system

# Technologies used

1. Java
2. SQLite

This actually uses an in-memory database, but if data needs to be persisted this could easily changed by creating a file on your local drive and pointing SQLite to use it instead.

# Setup

1. Clone this repo
2. Open in IDE of your choice (you can import this project as an existing maven project)
3. Use ./mvnw spring-boot:run to run

# Available Request Endpoints:

Create inventory item
  POST http://localhost:8080/inventories
Read all inventory items
  GET http://localhost:8080/inventories
Read single inventory item
  GET http://localhost:8080/inventories/1
Update inventory item
  PUT http://localhost:8080/inventories/1
Delete inventory item
  DELETE http://localhost:8080/inventories/1
Create order
  POST http://localhost:8080/orders
Read all orders
  GET http://localhost:8080/orders
Read single order
  GET http://localhost:8080/orders/1
Update order
  PUT http://localhost:8080/orders/1
Delete order
  DELETE http://localhost:8080/orders/1
