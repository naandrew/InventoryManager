package com.Inventory;

import java.util.*;

public class ReportGenerator{
    private ProductManager prodManager;
    private SupplierManager suppManager;

    public ReportGenerator(ProductManager prod, SupplierManager sup){
        this.prodManager = prod;
        this.suppManager = sup;
    }

    public void generateInvReportAll(){
        List<Product> allProducts = prodManager.allProducts();
        generateInvReport(allProducts,"Inventory Report");
    }

    public void generateInvReportCategory(String category){
        List<Product> allProductsCat = prodManager.allProductsByCategory(category);
        generateInvReport(allProductsCat,"Inventory Report by Category: " + category);
    }

    public void generateInvReportSupplier(int suppID){
        List<Product> allProductsSup = prodManager.allProductsBySupplier(suppID);
        String supplier = suppManager.getSupName(suppID);
        generateInvReport(allProductsSup, "Inventory Report by Supplier: " + supplier);
    }



    public void generateInvReport(List<Product> prod, String title){
        
        System.out.println(title);
        System.out.println("--------------------------");

        System.out.printf("%-5s %-20s %-10s %-10s %-15s%n", "ID", "Name", "Quantity", "Price", "Category");

        for(Product p : prod){
            System.out.printf("%-5d %-20s %-10d $%-9.2f %-15s%n", p.getID(), p.getName(), p.getNumUnits(), p.getPricePerUnit(), p.getCategory());
        }
        System.out.println();
    }

    public void generateValueReportAllProducts(){
        List<Product> allProducts = prodManager.allProducts();
        generateValueReport(allProducts, "Inventory Value Report");
    }

    public void generateValueReportCategory(String category){
        List<Product> allProductsCat = prodManager.allProductsByCategory(category);
        generateValueReport(allProductsCat, "Inventory Value Report by Category: " + category);
    }

    public void generateValueReportSupplier(int suppID){
        List<Product> allProductsSup = prodManager.allProductsBySupplier(suppID);
        String supplier = suppManager.getSupName(suppID);
        generateValueReport(allProductsSup, "Inventory Value Report by Supplier: " + supplier);
    }

    public void generateValueReport(List<Product> productList, String title){

        System.out.println("--------------------------");

        System.out.printf("%-20s %-10s %-12s %-15s%n", "Product", "Quantity", "Unit Price", "Total Value");

        double totalValue = 0;
        for(Product p : productList){
            double totalForProduct = p.getTotalWorth();
            totalValue += totalForProduct;
            
            System.out.printf("%-20s %-10d $%-11.2f $%-14.2f%n", p.getName(), p.getNumUnits(), p.getPricePerUnit(), totalForProduct);
           
        }
        System.out.printf("Total Inventory Value: $%.2f%n",totalValue);
        System.out.println();
    }

    public void generateLowStockReport(){
        List<Product> lowStock = prodManager.checkLowStockProducts();

        System.out.println("LOW STOCK WARNING");
        System.out.println();
        System.out.println("---------------");

        System.out.printf("%-5s %-20s %-10s %-18s%n", "ID", "Name", "Quantity", "Reorder Threshold");

        for(Product p : lowStock){
            System.out.printf("%-5d %-20s %-10d %-18d%n", p.getID(), p.getName(), p.getNumUnits(), p.getLowThreshold());
        }
    }



}