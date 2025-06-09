package com.Inventory;

import java.util.*;

public class SupplierManager{
    private Map<Integer, Supplier> suppliers = new HashMap<>();
    private DatabaseManager dbManager;
    
    public SupplierManager(DatabaseManager db){
        this.dbManager = db;
    }

    public Supplier getSuppByID(int suppID){
        return suppliers.get(suppID);
    }

    public int getSuppByName(String name){
        return dbManager.getSupplierByName(name);
    }

    public boolean supplierExists(int supID){
        return suppliers.containsKey(supID);
    }

    public String getSupName(int suppID){
        Supplier sup = suppliers.get(suppID);
        return sup.getName();
    }

    public int supplierAmount(){
        return suppliers.size();
    }

    public void showSuppliers(){
         System.out.println("--- Available Suppliers ---");
        List<Supplier> allSuppliers = getAllSupp();
        for(Supplier s : allSuppliers){
            System.out.println(s.toString());
        }
        System.out.println();
    }

    public void loadSuppFromDB(){
        List<Supplier> suppList = dbManager.getAllSuppliers();
        suppliers.clear();
        for(Supplier s : suppList){
            suppliers.put(s.getID(),s);
        }
    }

    public boolean addSupplier(Supplier supp){
        if(!dbManager.addSupplierToDB(supp)){
            System.out.println("Failed to add supplier to Database");
            return false;
        }
        suppliers.put(supp.getID(), supp);
        return true;
    }

    public List<Supplier> getAllSupp(){
        return dbManager.getAllSuppliers();
    }

    public boolean updateSupp(Supplier supp){
        if(!dbManager.updateSupplier(supp)){
            System.out.println("Failed to update supplier in Database");
            return false;
        }
        suppliers.put(supp.getID(), supp);
        return true;
    }

    public boolean deleteSupplier(int suppID){
        if(dbManager.suppExistsForProduct(suppID)){
            System.out.println("Cannot delete supplier: Product exists with given supplier");
            return false;
        }

        if(!dbManager.deleteSupplierFromDB(suppID)){System.out.println("Failed to remove supplier from Database");}
        if(suppliers.remove(suppID) == null){System.out.println("Supplier not found"); return false;}

      

        return true;
    }


}