package com.Inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.util.Set;


 public class DatabaseManager{
    private Connection conn;
    private String user;
    private String password;
    private String databaseName;

    public DatabaseManager(String name, String pswrd){
        this.user = name;
        this.password = pswrd;
        this.databaseName = "InventoryDB";
    }



//Connecting and disconnecting to DB
    public void connect(){ 
        String url = "jdbc:mysql://localhost:3306/" + databaseName;
         user = "root";
         password = "$Dcsd201052";

        try{
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("Connection successful!");
        
        }catch(SQLException e){
            System.out.println("Failed to connect!");
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void disconnect(){
        try{
            if(conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Disconnected from database");
            }
        }catch(SQLException e){
            System.out.println("Failed to disconnect!");
            e.printStackTrace();
        }

        }

    public boolean exists(String table, String colName, String name){
        Set<String> allowedTables = Set.of("Product","Supplier");
        Set<String> allowedColumns = Set.of("ProductName","SupplierName", "Category");

        if(!allowedTables.contains(table) || !allowedColumns.contains(colName)){
            throw new IllegalArgumentException("Invalid table or column name");
        }

        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + colName + " = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
             int count = rs.getInt(1);
             return count > 0;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

//Product Table

//Add product
    public boolean addProductToDB(Product prod){
        if(exists("Product", "ProductName", prod.getName())){
            System.out.println("Product already exists");
            return false;
        }
        String sql = "INSERT INTO Product (ProductName, Quantity, PricePerUnit, Category, SupplierID, LowStockThreshold) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, prod.getName());
            ps.setInt(2, prod.getNumUnits());
            ps.setDouble(3, prod.getPricePerUnit());
            ps.setString(4, prod.getCategory());
            ps.setInt(5, prod.getSuppID());
            ps.setInt(6,prod.getLowThreshold());

            int rowsAffected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                int prodID = rs.getInt(1);
                prod.setProductID(prodID);
            }
            return rowsAffected == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProductFromDB(int prodID){
        String sql = "DELETE FROM Product WHERE ProductID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,prodID);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected >= 1){System.out.println("Product successfully deleted"); return true;}
            else{System.out.println("Product not found"); return false;}
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    private Product ResultSetProduct(ResultSet rs) throws SQLException{
        return new Product(rs.getInt("ProductID"), rs.getString("ProductName"), rs.getString("Category"), 
        rs.getDouble("PricePerUnit"), rs.getInt("Quantity"),rs.getInt("SupplierID"), rs.getInt("LowStockThreshold"));
    }

    public Product getProductByID(int prodID){
        String sql = "SELECT * FROM Product WHERE ProductID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,prodID);
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
            return ResultSetProduct(rs);
            }
            else{System.out.println("Product not found");}
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
     }
    

    public boolean updateProduct(Product prod){
        String sql = "UPDATE Product SET ProductName = ?, Quantity = ?, PricePerUnit = ?, Category = ?, SupplierID = ?, LowStockThreshold = ? WHERE ProductID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,prod.getName());
            ps.setInt(2,prod.getNumUnits());
            ps.setDouble(3,prod.getPricePerUnit());
            ps.setString(4,prod.getCategory());
            if(!supplierExists(prod.getSuppID())){
                System.out.println(prod.getSuppID());
                System.out.println("Supplier does not exist");
                return false;
            }
            ps.setInt(5,prod.getSuppID());
            ps.setInt(6,prod.getID());
            ps.setInt(7,prod.getLowThreshold());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProductQuantity(int prodID, int newQuantity){
        String sql = "UPDATE Product SET Quantity = ? WHERE ProductID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,newQuantity);
            ps.setInt(2,prodID);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1){return true;}
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Product> getAllProducts(){
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Product";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product p = ResultSetProduct(rs);
                productList.add(p);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return productList;
    }

    public List<Product> getAllCategoryProducts(String category){
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE Category = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,category);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product p = ResultSetProduct(rs);
                productList.add(p);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return productList;
    }

    public List<Product> getAllProductsBySupplier(int supID){
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,supID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product p = ResultSetProduct(rs);
                productList.add(p);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return productList;
    }

    public boolean suppExistsForProduct(int suppID){
        String sql = "SELECT COUNT(*) FROM Product WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,suppID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return count > 0;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean categoryExistsForProduct(String cat){
        return exists("Product","Category", cat);
    }

    //Supplier Table

    public boolean supplierExists(int suppID){
        String sql = "SELECT COUNT(*) FROM Supplier WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return count > 0;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSupplierToDB(Supplier sup){
        if(exists("Supplier", "SupplierName", sup.getName())){
            System.out.println("Supplier already exists");
            return false;
        }
        String sql = "INSERT INTO Supplier (SupplierName, ContactInfo) " + "VALUES (?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,sup.getName());
            ps.setString(2, sup.getInfo());
            int rowsAffected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
            int suppID = rs.getInt(1);
            sup.setSupID(suppID);
            }
            return rowsAffected == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private Supplier ResultSetSupplier(ResultSet rs) throws SQLException{
        return new Supplier(rs.getInt("SupplierID"), rs.getString("SupplierName"), rs.getString("ContactInfo"));
    }

    public Supplier getSupplierByID(int supID){
        String sql = "SELECT * FROM SUPPLIER WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,supID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return ResultSetSupplier(rs);
            }
            else{System.out.println("Product not found");}
        }catch(SQLException e){
            e.printStackTrace();
        }
    
    return null;
    }

    public int getSupplierByName(String name){
        String sql = "SELECT * FROM Supplier WHERE SupplierName = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("ProductID");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateSupplier(Supplier sup){
        String sql = "UPDATE Supplier SET SupplierName = ?, ContactInfo = ? WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,sup.getName());
            ps.setString(2,sup.getInfo());
            ps.setInt(3,sup.getID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplierFromDB(int supID){
        String sql = "DELETE FROM Supplier WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,supID);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0){System.out.println("Product successfully deleted"); return true;}
            else{System.out.println("Product not found"); return false;}
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Supplier> getAllSuppliers(){
        List<Supplier> supplierList = new ArrayList<>();
        String sql = "Select * FROM Supplier";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Supplier s = ResultSetSupplier(rs);
                supplierList.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return supplierList;
    }

    //Order Table

    public boolean addOrder(Order ord){
        String sql = "INSERT INTO Orders (QuantityOrdered, OrderDate, DeliveryDate, ProductID, SupplierID) " + "VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,ord.getQuantityOrdered());
            ps.setDate(2,ord.getOrdDate());
            ps.setDate(3,ord.getDelivDate());
            ps.setInt(4,ord.getProdID());
            ps.setInt(5,ord.getSuppID());
            int rowsAffected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                int ordID = rs.getInt(1);
                ord.setOrderID(ordID);
            }
            return rowsAffected == 1;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        } 
    }

    public boolean deleteOrder(int ordID){
        String sql = "DELETE FROM Orders WHERE OrderID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,ordID);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0){System.out.println("Order was successfully deleted"); return true;}
            else{System.out.println("Order not found"); return false;}
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private Order ResultSetOrder(ResultSet rs) throws SQLException{
        return new Order(rs.getInt("OrderID"), rs.getInt("ProductID"), rs.getInt("SupplierID"), rs.getInt("QuantityOrdered"),rs.getDate("OrderDate"), rs.getDate("DeliveryDate"), rs.getString("Status"));
    }

    public Order getOrderByID(int ordID){
        String sql = "SELECT * FROM Orders WHERE OrderID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,ordID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return ResultSetOrder(rs);
            }
            else{System.out.println("Order not found");}
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getOrdersByProduct(int prodID){
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE ProductID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,prodID);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Order ord = ResultSetOrder(rs);
                orderList.add(ord);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Order> getOrdersBySupplier(int suppID){
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE SupplierID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Order o = ResultSetOrder(rs);
                orderList.add(o);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return orderList;
    }

    public boolean updateDeliveryDate(int ordID, Date newDate){
        String sql = "UPDATE Orders SET DeliveryDate = ? WHERE OrderID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDate(1,newDate);
            ps.setInt(2,ordID);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1){return true;}
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDeliveryStatus(int ordID, String status){
        String sql = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,status);
            ps.setInt(2,ordID);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1){return true;}
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public List<Order> getAllOrders(){
        return getAllOrdersUniversal("");
    }

    public List<Order> getAllOrdersPending(){
        return getAllOrdersUniversal("Pending");
    }

    public List<Order> getAllOrdersDelivered(){
        return getAllOrdersUniversal("Delivered");
    }

    public List<Order> getAllOrdersUniversal(String status){
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM Orders";
        if(!status.isEmpty()){
            sql += " WHERE Status = " + status;
        }

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Order ord = ResultSetOrder(rs);
                orderList.add(ord);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return orderList;
    }
    
}

    

