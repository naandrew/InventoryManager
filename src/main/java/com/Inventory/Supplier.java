package com.Inventory;

public class Supplier{
    private int supplierID;
    private String name;
    private String contactInfo;

    public Supplier(int suppID, String name, String info){
        this.supplierID = suppID;
        this.name = name;
        this.contactInfo = info;
    }

    public Supplier(String name, String info){
        this.name = name;
        this.contactInfo = info;
    }

    public int getID(){return supplierID;}
    public String getName(){return name;}
    public String getInfo(){return contactInfo;}

    public void setSupID(int newID){
        this.supplierID = newID;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setInfo(String newInfo){
        this.contactInfo = newInfo;
    }

    public String toString(){
        return "ID: " + supplierID + " | Name: " + name + " |  Contact info: " + contactInfo;
    }


}