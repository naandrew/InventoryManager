package com.Inventory;


import java.util.Scanner;

public class Main {
    public static final Scanner scanner = new Scanner(System.in);
    //Main menu
   public static void showMenu(){
        System.out.println("1. Product Management");
        System.out.println("2. Orders Management");
        System.out.println("3. Supplier Management");
        System.out.println("4. Reports");
        System.out.println("5. Exit Program");
        System.out.println("--------------------");
    }

    private static int validChoice(){
        String input;
        while(true){
        System.out.print("Enter your choice: ");
            input = scanner.nextLine();  
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Try again.");
            continue;
        }
        }
    }
    
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager("root","$Dcsd201052");
         dbManager.connect();
        
        ProductManager prodManager = new ProductManager(dbManager);
        prodManager.loadProdFromDB();

        SupplierManager supManager = new SupplierManager(dbManager);
        supManager.loadSuppFromDB();

        OrderManager ordManager = new OrderManager(dbManager);
        ordManager.loadOrdFromDB();

        ReportGenerator repGenerator = new ReportGenerator(prodManager,supManager);

       

        

        System.out.println("Inventory Manager");
        System.out.println("--------------------------");
        System.out.println();


        int choice;
        do{
            showMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
        
                choice1(prodManager,supManager,repGenerator);
                break;
                case 2:
                choice2(prodManager, supManager, ordManager);
                break;
                case 3:
                choice3(supManager);
                break;
                case 4:
                choice4(prodManager, supManager, repGenerator);
                break;
                case 5:
                System.out.println("Exiting Program");
                break;
                default:
                System.out.println("Invalid input");
                break;
            }

        }while(choice != 5);

        dbManager.disconnect();
        scanner.close();

    }

 
    //Product Menu
    public static void showMenu1(){
        System.out.println("--- Product Menu ----");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product");
        System.out.println("4. Delete Product");
        System.out.println("5. Return to Main Menu");
        System.out.println("---------------------");
    }
   
    //Product Managment
    public static void choice1(ProductManager prod, SupplierManager sup, ReportGenerator rep){
        int choice;
        do{
            showMenu1();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
            case 1:
                addProd(prod,sup);
                break;
            case 2:
                viewProducts(prod,rep, sup);
                break;
            case 3:
                updateProduct(prod, rep, sup);
                break;
            case 4:
                deleteProduct(prod, rep);
                break;
            case 5:
                System.out.println("Returning to main menu");
                break;
            default:
                System.out.println("Invalid input");
                break;
         }

        }while(choice != 5);
    }
    //Check if user choice (y/n) is valid
    public static boolean checkChoice(String question){
        System.out.println(question);
    
        while(true){
            String input = scanner.nextLine();
            if(input.equalsIgnoreCase("y")){return true;}
            else if(input.equalsIgnoreCase("n")){return false;}
    
            else{System.out.println("Please enter (y/n)");}
            
        }
    }
    //Check if int input valid
    public static int validInt(String label, int min, int max){
        while(true){
            System.out.print(label);
            try{
            int input = scanner.nextInt();
            scanner.nextLine();
            if(input < min){
                System.out.println("Please enter a number greater than " + min);

            }
            else if(input > max){
                System.out.println("Please enter a number less than " + max);
            }
            else{
                return input;
            }
        
            }catch(NumberFormatException e){
                System.out.println("Please enter a valid number");
            }

        }
    }
    //Check if supplier is valid
    public static int validSupplier(SupplierManager sup, String label){
        int supplierID;  
        do{
            System.out.println(label);
            supplierID = scanner.nextInt();
            scanner.nextLine();
            if(!sup.supplierExists(supplierID)){
                System.out.println("Supplier does not exist");
            }
        }while(!sup.supplierExists(supplierID));
        return supplierID;
    }
    //Check if product is valid
    public static int validProduct(ProductManager prod, String label){
        int prodID;
        do{
            System.out.println(label);
            prodID = scanner.nextInt();
            scanner.nextLine();
            if(!prod.productExists(prodID)){
                System.out.println("Product does not exist");
            }
        }while(!prod.productExists(prodID));
        return prodID;
    }

    //Check if order is valid
    public static int validOrder(OrderManager ord, String label){
        int ordID;
        do{
            System.out.println(label);
            ordID = scanner.nextInt();
            scanner.nextLine();
            if(!ord.orderExists(ordID)){
                System.out.println("Order does not exist");
            }
        }while(!ord.orderExists(ordID));
        return ordID;
    }
//Check if category is valid
    public static String validCategory(ProductManager prod, String label){
        String category;
        do{
            System.out.println(label);
            category = scanner.nextLine();
            if(!prod.categoryExists(category)){
                System.out.println("Category not found");
            }
        }while(!prod.categoryExists(category));
        return category;
    }

    //Add order
    public static void addProd(ProductManager prod, SupplierManager sup){
        String name, category;
        int units, lowStockThresh, supplierID;
        double price;

        System.out.println("Enter product name: ");
        name = scanner.nextLine();
        System.out.println("Enter product category: ");
        category = scanner.nextLine();
        System.out.println("Enter product price: ");
        price = scanner.nextDouble();
        scanner.nextLine();
       
        units = validInt("Enter product amount: ", 0, 1000);
        
        lowStockThresh = validInt("Enter low stock threshold of product: ", 2, 50);
        
        if(checkChoice("Show available suppliers? (y/n)")){
            sup.showSuppliers();
        }

        supplierID = validSupplier(sup, "Enter product supplier's id: ");
        

        Product p = new Product(name, category, price, units, lowStockThresh, supplierID);
        
        if(prod.addProduct(p)){
            System.out.println("Product added successfully!");
        }else{System.out.println("Failed to add product");}

    }

    public static void deleteProduct(ProductManager prod, ReportGenerator rep){
        if(checkChoice("Show products? (y/n)")){
            prod.showProducts();
        }
        int prodID = validProduct(prod, "Enter product ID: ");
        if(prodID == -1){return;}
        
        if(prod.removeProduct(prodID)){
            System.out.println("Product successfuly removed");
        }else{System.out.println("Failed to remove product");}
    }

    public static void updateProductMenu(){
        System.out.println("What would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Category");
        System.out.println("3. Price");
        System.out.println("4. Amount");
        System.out.println("5. Supplier");
        System.out.println("6. Done");
        System.out.println("------------------------------");
    }

    public static void updateProduct(ProductManager prod, ReportGenerator rep, SupplierManager sup){
        if(checkChoice("Show Products? (y/n)")){
            prod.showProducts();
        }
        int prodID = validProduct(prod, "Enter the ID of the product you wish to update: ");
        

        Product p = prod.getProdByID(prodID);

        p.toString();

        updateProductMenu();

        int choice;
        do{
           
            choice = validChoice();
           

            switch(choice){
            case 1:
                System.out.println("Enter a new name: ");
                String name = scanner.nextLine();
                p.setName(name);
                break;
            case 2:
                System.out.println("Enter a new category: ");
                String category = scanner.nextLine();
                p.setCategory(category);
                break;
            case 3:
                System.out.println("Enter product price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();
                p.setPricePerUnit(price);
                break;
            case 4:
                int newNum = validInt("Enter a new amount: ", 0, 1000);
                p.setQuantity(newNum);
                break;
            case 5:
                int supplierID = validSupplier(sup, "Enter product supplier's id: ");
                p.setSupplierID(supplierID);
                break;
            case 6:
                break;
            default:
                System.out.println("Enter a valid number (1-6)");
                break;
            }
        }while(choice != 6);

        if(prod.updateProd(p)){
            System.out.println("Product updated successfuly!");
        }else{System.out.println("Failed to update");}


    }

    public static void viewProductMenu(){
        System.out.println("1. View all products");
        System.out.println("2. View all products by category");
        System.out.println("3. View all products by supplier");
        System.out.println("4. Return to Product Menu");
        System.out.println("--------------------");
    }

    public static void viewProducts(ProductManager prod, ReportGenerator rep, SupplierManager sup){
        
        int choice;
        do{
            viewProductMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    rep.generateValueReportAllProducts();
                    break;
                case 2:
                    String category = validCategory(prod, "Enter a category: ");

                    rep.generateInvReportCategory(category);
                    break;
                case 3:
            
                    if(checkChoice("Show suppliers? (y/n)")){
                    sup.showSuppliers();
                    }
                    int suppID = validSupplier(sup, "Enter supplier ID: ");

                    rep.generateInvReportSupplier(suppID);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Enter a valid num (1-4)");
                    break;
            }

        }while(choice != 4);
    }

    //Orders

     //Order Menu
    public static void showMenu2(){
        System.out.println("--- Order Menu ---");
        System.out.println("1. Create Order");
        System.out.println("2. Cancel/Remove Order");
        System.out.println("3. View Orders");
        System.out.println("4. Mark Order as Completed");
        System.out.println("5. Return to Main Menu");
        System.out.println("------------------");
    }
//Choice 2
    public static void choice2(ProductManager prod, SupplierManager sup, OrderManager ord){
        int choice;
        do{
            showMenu2();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    createOrd(prod, sup, ord);
                    break;
                case 2:
                    removeOrd(ord);
                    break;
                case 3:
                    viewOrders(prod, ord, sup);
                    break;
                case 4:
                    markOrderAsComplete(ord);
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Enter a valid num (1-5)");
                    break;
            }

        }while(choice != 5);
    }
    //Create Order
    public static void createOrd(ProductManager prod, SupplierManager sup,OrderManager ord){
        System.out.println();

        if(checkChoice("Show Products? (y/y)")){
            prod.showProducts();
        }
        int prodID = validProduct(prod, "Enter the product ID: ");
        
        if(checkChoice("Show Suppliers? (y/n)")){
            sup.showSuppliers();
        }

        int suppID = validSupplier(sup, "Enter the supplier ID: ");
       
        Product p = prod.getProdByID(prodID);

        System.out.println("Enter the quantity you want to order: ");
        int quantity = validInt("Enter the quantity you want to order: ", 1, 1000 - p.getNumUnits());

        if(ord.createOrder(prodID, suppID, quantity)){
            System.out.println("Order successfully placed!");
        }else{System.out.println("Failed to place order");}
        

    }
    //Remove order
    public static void removeOrd(OrderManager ord){
        System.out.println();

        if(checkChoice("Show Orders? (y/n)")){
            ord.showOrders();
        }

        int ordID = validOrder(ord, "Enter the order ID: ");
        
        ord.removeOrder(ordID);
        System.out.println("Order successfully removed");
    
    }
    //View orders
    public static void viewOrders(ProductManager prod, OrderManager ord, SupplierManager supp){
        System.out.println("1. View all orders");
        System.out.println("2. View all orders by product");
        System.out.println("3. View all orders by supplier");
        System.out.println("4. View active orders");
        System.out.println("5. View past orders");
        System.out.println("6. Return to Order Menu");
        int choice;
        do{
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    ord.showOrders();
                    break;
                case 2:

                    if(checkChoice("Show products? (y/n)")){
                    prod.showProducts();
                    }
                    int prodID = validProduct(prod, "Enter the product ID: ");
                    
                    ord.showOrdersByProd(prodID);
                    break;
                case 3:
                    if(checkChoice("Show suppliers? (y/n) ")){
                        supp.showSuppliers();
                    }
                    int supID = validSupplier(supp, "Enter the supplier ID: ");
                
                    ord.showOrdersBySupp(supID);
                    break;
                case 4:
                    ord.showOrdersPending();
                    break;
                case 5:
                    ord.showOrdersPending();
                    break;
                case 6:
                    
                default:
                    System.out.println("Enter a valid choice (1-4)");
                    break;

            }

        }while(choice != 6);

    }
    //Complete order
    public static void markOrderAsComplete(OrderManager ord){
        if(checkChoice("Show pending orders? (y/n)")){
            ord.showOrdersPending();
        }

        int ordID = validOrder(ord,"Enter the order ID: ");
       

        ord.markOrderAsRecieved(ordID);
    }

//Suppliers
    //Supplier menu
    public static void showMenu3(){
        System.out.println("--- Supplier Menu ---");
        System.out.println("1. Add Supplier");
        System.out.println("2. Remove Supplier");
        System.out.println("3. Update Suppliers");
        System.out.println("4. View Suppliers");
        System.out.println("5. Return to Main Menu");
        System.out.println("---------------------");
    }
//Choice 3
    public static void choice3(SupplierManager supp){
        int choice;
        do{
            showMenu3();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    addSupplier(supp);
                    break;
                case 2:
                    removeSupplier(supp);
                    break;
                case 3:
                    updateSupplier(supp);
                    break;
                case 4:
                    supp.showSuppliers();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Enter a valid choice (1-5)");
                    break;
            }

        }while(choice != 5);
    }

    
    //Add supplier
    public static void addSupplier(SupplierManager supp){
        String name, info;
        System.out.println("Enter the supplier's name: ");
        name = scanner.nextLine();

        System.out.println("Enter the supplier's contact info: ");
        info = scanner.nextLine();

        Supplier s = new Supplier(name,info);
        if(supp.addSupplier(s)){System.out.println("Supplier added successfully!");}

    }

    public static void removeSupplier(SupplierManager supp){
        if(checkChoice("Show suppliers? (y/n) ")){
            supp.showSuppliers();
        }

        int suppID = validSupplier(supp, "Enter the supplier ID: ");
        if(supp.deleteSupplier(suppID)){System.out.println("Removed supplier successfully!");}
        
    }

 

    public static void updateSupplier(SupplierManager supp){
        if(checkChoice("Show suppliers? (y/n)")){
            supp.showSuppliers();
        }

        int suppID = validSupplier(supp, "Enter the supplier ID: ");

        Supplier sup = supp.getSuppByID(suppID);

       


        System.out.println("1. Change name");
        System.out.println("2. Change info");
        System.out.println("3. Done");
        int choice = 0;

        do{
            System.out.println("1. Change name");
            System.out.println("2. Change info");
            System.out.println("3. Done");
            choice = validChoice();

            switch(choice){
                case 1:
                    System.out.println("Enter the supplier's new name: ");
                    String newName = scanner.nextLine();
                    sup.setName(newName);
                    break;
                case 2:
                    System.out.println("Enter the supplier's new info: ");
                    String newInfo = scanner.nextLine();
                    sup.setInfo(newInfo);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Enter valid choice (1-3)");
                    break;
            }
        
        }while(choice != 3);

        if(supp.updateSupp(sup)){System.out.println("Successfuly added supplier");}
    }


//Reports

//Report Menu
    public static void showMenu4(){
        System.out.println("--- Reports Menu ---");
        System.out.println("1. Inventory Reports");
        System.out.println("2. Value Reports");
        System.out.println("3. Low Stock Reports");
        System.out.println("4. Return to Main Menu");
        System.out.println("--------------------");
    }

//Choice 4

    public static void choice4(ProductManager prod, SupplierManager supp, ReportGenerator rep){
        int choice;
        do{
            showMenu4();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    showInvReport(prod, supp, rep);
                    break;
                case 2:
                    showValueReport(prod, supp, rep);
                    break;
                case 3:
                    rep.generateLowStockReport();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Enter a valid num (1-4)");
                    break;
            }

        }while(choice != 4);
    }

    //Menu for Inventory report
    public static void showInvReportMenu(){
        System.out.println("1. Get inventory report of all products");
        System.out.println("2. Get inventory report based on category");
        System.out.println("3. Get inventory report based on supplier");
        System.out.println("4. Return to Reports Menu");
        System.out.println("---------------------------------------");
    }

    //Inventory report
    public static void showInvReport(ProductManager prod, SupplierManager supp, ReportGenerator rep){
        int choice;
        do{
            showInvReportMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    rep.generateInvReportAll();
                    break;
                case 2:
                    String category = validCategory(prod, "Enter the category: ");
                    rep.generateInvReportCategory(category);
                    break;
                case 3:
                    if(checkChoice("View Suppliers?")){
                        supp.showSuppliers();
                    }
                    int suppID = validSupplier(supp, "Enter the supplier ID: ");
                    rep.generateInvReportSupplier(suppID);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Enter a valid num (1-4)");
                    break;
            }

        }while(choice != 4);
    }

    //Value report menu
    public static void showValueReportMenu(){
        System.out.println("1. Get value report of all products");
        System.out.println("2. Get value report based on category");
        System.out.println("3. Get value report based on supplier");
        System.out.println("4. Return to Reports Menu");
        System.out.println("-----------------------------------");
    }

    //Value report

    public static void showValueReport(ProductManager prod, SupplierManager supp, ReportGenerator rep){
        int choice;
        do{
            showValueReportMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch(choice){
                case 1:
                    rep.generateValueReportAllProducts();
                    break;
                case 2:
                    String category = validCategory(prod, "Enter the category: ");
                    rep.generateValueReportCategory(category);
                    break;
                case 3:
                    if(checkChoice("Show suppliers?")){
                        supp.showSuppliers();
                    }
                    int suppID = validSupplier(supp, "Enter supplier ID: ");
                    rep.generateValueReportSupplier(suppID);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Enter a valid num (1-4)");
                    break;
            }
        }while(choice != 4);
    }

}