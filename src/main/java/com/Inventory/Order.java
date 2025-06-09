package com.Inventory;
import java.sql.Date;


public class Order{
    private int orderID;
    private int productID;
    private int supplierID;
    private int quantityOrdered;
    private Date orderDate;
    private Date deliveryDate;
    private String status;

    public Order(int prodID, int suppID, int quan, Date ordDate, Date delivDate){
        this.productID = prodID;
        this.supplierID = suppID;
        this.quantityOrdered = quan;
        this.orderDate = ordDate;
        this.deliveryDate = delivDate;
        this.status = "Pending";
    }

    public Order(int ordID, int prodID, int suppID, int quan, Date ordDate, Date delivDate, String status){
        this.orderID = ordID;
        this.productID = prodID;
        this.supplierID = suppID;
        this.quantityOrdered = quan;
        this.orderDate = ordDate;
        this.deliveryDate = delivDate;
        this.status = status;
    }

    public int getOrdID(){return this.orderID;}
    public int getProdID(){return this.productID;}
    public int getSuppID(){return this.supplierID;}
    public int getQuantityOrdered(){return this.quantityOrdered;}
    public Date getOrdDate(){return this.orderDate;}
    public Date getDelivDate(){return this.deliveryDate;}
    public String getStatus(){return this.status;}
    public boolean isRecieved(){return this.status == "Delivered" ? true : false;}
    public void setOrderID(int newID){this.orderID = newID;}
    public void setStatus(String newStatus){this.status = newStatus;}
    public void setDelivDate(Date newDate){this.deliveryDate = newDate;}

    public String toString() {
    return  "-----------------------------\n" +
            "Order ID:            " + orderID + "\n" +
            "Product ID:          " + productID + "\n" +
            "Supplier ID:         " + supplierID + "\n" +
            "Quantity Ordered:    " + quantityOrdered + "\n" +
            "Order Date:          " + orderDate + "\n" +
            "Delivery Date:       " + deliveryDate + "\n" +
            "Status:              " + status + "\n" +
            "-----------------------------";
    }

}