package com.Inventory;

import java.util.*;


public class ProductManager{
    private Map<Integer, Product> products = new HashMap<>();
    private DatabaseManager dbManager;
    private OrderManager ordManager;

    public ProductManager(DatabaseManager db){
        this.dbManager = db;
    }

    public Product getProdByID(int prodID){
        return products.get(prodID);
    }

    public String getProdName(int prodID){
        Product p = products.get(prodID);
        return p.getName();
    }

    public int productAmount(){
        return products.size();
    }

    public boolean productExists(int prodID){
        return products.containsKey(prodID);
    }

    public boolean categoryExists(String cat){
        return dbManager.categoryExistsForProduct(cat);
    }

    public void increaseProductQuantity(int prodID, int amount){
        Product p = getProdByID(prodID);
        if(p != null){
        p.setQuantity(p.getNumUnits() + amount);
        }
    }

    public boolean updateProd(Product p){
         if(!dbManager.updateProduct(p)){return false;}

         products.put(p.getID(), p);
         return true;
    }


    public void showProducts(){
        System.out.println("--- Available Products ---");
        List<Product> allProducts = allProducts();
        for(Product p : allProducts){
            System.out.println(p.toString());
        }
        System.out.println();
    }

    public List<Product> allProducts(){
        return dbManager.getAllProducts();
    }

    public List<Product> allProductsByCategory(String category){
        return dbManager.getAllCategoryProducts(category);
    }

    public List<Product> allProductsBySupplier(int suppID){
        return dbManager.getAllProductsBySupplier(suppID);
    }

    public void loadProdFromDB(){
        List<Product> prodList = dbManager.getAllProducts();
        products.clear();
        for(Product p : prodList){
            products.put(p.getID(),p);
        }
    }

    public boolean addProduct(Product p){
        if(!dbManager.addProductToDB(p)){
            System.out.println("Failed to add product to Database");
            return false;
        }
        products.put(p.getID(), p);
        return true;
    }

    public boolean removeProduct(int prodID){
        if(!dbManager.deleteProductFromDB(prodID)){
            System.out.println("Failed to remove product from Database");
            return false;
        }
        if(products.remove(prodID) == null){System.out.println("Product not found"); return false;}

        
        return true;
    }

    public List<Product> checkLowStockProducts(){
        List<Product> lowStock = new ArrayList<>();
        for(Map.Entry<Integer,Product> entry : products.entrySet()){
            Product p = entry.getValue();
            if(p.getNumUnits() <= p.getLowThreshold()){
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    public boolean restockProduct(int prodID, int suppID, int amount){
        if(!products.containsKey(prodID)){
            System.out.println("Product not found"); 
            return false;
        }
        ordManager.createOrder(prodID,suppID, amount);
        return true;
    }
}