package com.Inventory;

public class Product{
    private int productID;
    private String name;
    private String category;
    private double price_per_unit;
    private int units;
    private int lowStockThreshold;
    private int supplierID;
    
    public Product(int prodID, String name, String cat, double price, int unit, int lowStock, int suppID){
        this.productID = prodID;
        this.name = name;
        this.category = cat;
        this.price_per_unit = price;
        this.units = unit;
        this.supplierID = suppID;
        this.lowStockThreshold = lowStock;
    }

    public Product(String name, String cat, double price, int unit, int lowStock, int suppID){
        this.name = name;
        this.category = cat;
        this.price_per_unit = price;
        this.units = unit;
        this.supplierID = suppID;
        this.lowStockThreshold = lowStock;
    }

    public int getID(){return productID;}

    public String getName(){return name;}
    public String getCategory(){return category;}
    public double getPricePerUnit(){return price_per_unit;}
    public int getNumUnits(){return units;}
    public double getTotalWorth(){return price_per_unit * units;}
    public int getLowThreshold(){return lowStockThreshold;}
    public int getSuppID(){return supplierID;}


    public void setName(String newName){
        this.name = newName;
    }

    public void setPricePerUnit(double newPPU){
        this.price_per_unit = newPPU;
    }

    public void setQuantity(int newUnits){
        this.units = newUnits;
    }

    public void setThreshold(int newThreshold){
        this.lowStockThreshold = newThreshold;
    }
    public void setCategory(String newCat){
        this.category = newCat;
    }

    public void setProductID(int newID){
        this.productID = newID;
    }

    public void setSupplierID(int newID){
        this.supplierID = newID;
    }

    public String toString() {
        return "ProdID: " + productID + " | Name: " + name + " | Price: $" + price_per_unit;
    }

    public boolean equals(Object obj){
        if(this == obj){return true;}
        if(obj == null || getClass() != obj.getClass()){return false;}
        Product other = (Product) obj;
        return productID == other.productID;
    }

}