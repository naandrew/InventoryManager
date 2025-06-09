package com.Inventory;

import java.util.*;
import java.sql.Date;
import java.time.LocalDate;

public class OrderManager{
    private Map<Integer, Order> orders = new HashMap<>();
    private DatabaseManager dbManager;
    private ProductManager prodManager;
    private SupplierManager suppManager;

    public OrderManager(DatabaseManager db){
        this.dbManager = db;
    }

    public void loadOrdFromDB(){
        List<Order> ordList = dbManager.getAllOrders();
        orders.clear();
        for(Order o : ordList){
            orders.put(o.getOrdID(), o);
        }
    }

    public int orderAmount(){
        return orders.size();
    }


    public boolean createOrder(int prodID, int suppID, int quantityOrdered){

        LocalDate todayLocal = LocalDate.now();
        LocalDate nextWeekLocal = todayLocal.plusDays(7);
        
        Date today = Date.valueOf(todayLocal);
        Date nextWeek = Date.valueOf(nextWeekLocal);


        
        Order ord = new Order(prodID, suppID, quantityOrdered, today, nextWeek);
        if(!dbManager.addOrder(ord)){
            System.out.println("Failed to add order to Database");
            return false;
        }
        orders.put(ord.getOrdID(),ord);
        return true;
    }

    public boolean removeOrder(int ordID){
        if(!dbManager.deleteOrder(ordID)){
            System.out.println("Failed to delete order from Database");
            return false;
        }

        if(orders.remove(ordID) == null){
            System.out.println("Order not found");
            return false;
        }
        return true;
    }

    public void showOrders(){
        System.out.println("--- Order History ---");
        List<Order> orders = AllOrders();
        for(Order o : orders){
            System.out.println(o.toString());
        }
        System.out.println();
    }

    public void showOrdersByProd(int prodID){
        System.out.println("Order History of " + prodManager.getProdName(prodID));
        List<Order> orders = getOrdersByProd(prodID);
        for(Order o : orders){
            System.out.println(o.toString());
        }
        System.out.println();
    }
     
    public void showOrdersBySupp(int suppID){
        System.out.println("Order History with " + suppManager.getSupName(suppID));
        List<Order> orders = getOrdersBySupplier(suppID);
        for(Order o : orders){
            System.out.println(o.toString());
        }
        System.out.println();
    }

    public void showOrdersPending(){
        System.out.println("--- Orders Pending ---");
        List<Order> orderList = allOrdersPending();

        for(Order o : orderList){
            System.out.println(o.toString());
        }
        System.out.println();
    }

    public void showOrdersDelivered(){
        System.out.println("--- Orders Pending ---");
        List<Order> orderList = allOrdersDelivered();

        for(Order o : orderList){
            System.out.println(o.toString());
        }
        System.out.println();
    }

    public Order getOrdByID(int ordID){
        return orders.get(ordID);
    }

    public boolean orderExists(int ordID){
        return orders.containsKey(ordID);
    }

    public List<Order> getOrdersByProd(int prodID){
        return dbManager.getOrdersByProduct(prodID);
    }

    public List<Order> AllOrders(){
        return dbManager.getAllOrders();
    }

    public List<Order> getOrdersBySupplier(int suppID){
        return dbManager.getOrdersBySupplier(suppID);
    }


    public List<Order> allOrdersPending(){
        return dbManager.getAllOrdersPending();
    }

    public List<Order> allOrdersDelivered(){
        return dbManager.getAllOrdersDelivered();
    }

    public boolean markOrderAsRecieved(int ordID){
        
        Order ord = orders.get(ordID);
        if(ord != null && !ord.isRecieved()){
            ord.setStatus("Delivered");

            int prodID = ord.getProdID();
            
            Product p = prodManager.getProdByID(prodID);

            int newAmount = p.getNumUnits() + ord.getQuantityOrdered();

            prodManager.increaseProductQuantity(prodID, newAmount);


            dbManager.updateDeliveryStatus(ordID,"Delivered");
    
            dbManager.updateProductQuantity(prodID, newAmount);
            return true;
        }
        System.out.println("Order not found or already recieved");
        return false;
    }
}