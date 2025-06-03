package db.dao;

import db.DatabaseConnection;
import rating.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer operations
 */
public class CustomerDao {
    
    /**
     * Get customer by phone number
     */
    public Customer getCustomerByPhoneNumber(String phoneNumber) throws SQLException {
        String query = "SELECT * FROM customers WHERE phone_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, phoneNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    
                    long ratePlanId = rs.getLong("rate_plan_id");
                    if (!rs.wasNull()) {
                        customer.setRatePlanId(ratePlanId);
                    }
                    
                    return customer;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Long customerId) throws SQLException {
        String query = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    
                    long ratePlanId = rs.getLong("rate_plan_id");
                    if (!rs.wasNull()) {
                        customer.setRatePlanId(ratePlanId);
                    }
                    
                    return customer;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setName(rs.getString("name"));
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                
                long ratePlanId = rs.getLong("rate_plan_id");
                if (!rs.wasNull()) {
                    customer.setRatePlanId(ratePlanId);
                }
                
                customers.add(customer);
            }
        }
        
        return customers;
    }
    
    /**
     * Get customers by rate plan ID
     */
    public List<Customer> getCustomersByRatePlanId(Long ratePlanId) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers WHERE rate_plan_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, ratePlanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getLong("id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    customer.setRatePlanId(rs.getLong("rate_plan_id"));
                    
                    customers.add(customer);
                }
            }
        }
        
        return customers;
    }
}