/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import db.dao.RatedCDRDao;
import rating.RatedCDR;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles invoice-related data transfer operations
 */
public class InvoiceDataTransfer {
    
    /**
     * Create the invoice table if it doesn't exist
     */
    public static void createInvoiceTable() throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS invoices (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                customer_id BIGINT NOT NULL,
                invoice_date DATETIME NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) NOT NULL,
                created_at DATETIME NOT NULL,
                FOREIGN KEY (customer_id) REFERENCES customers(id)
            )
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }
    
    /**
     * Create the customer invoice table if it doesn't exist
     */
    public static void createCustomerInvoiceTable() throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS customer_invoices (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                invoice_id BIGINT NOT NULL,
                customer_id BIGINT NOT NULL,
                service_type VARCHAR(20) NOT NULL,
                usage_amount INT NOT NULL,
                charge_amount DECIMAL(10,2) NOT NULL,
                created_at DATETIME NOT NULL,
                FOREIGN KEY (invoice_id) REFERENCES invoices(id),
                FOREIGN KEY (customer_id) REFERENCES customers(id)
            )
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }
    
    /**
     * Transfer rated CDRs to invoice tables
     */
    public static void transferRatedCDRsToInvoices() throws SQLException {
        RatedCDRDao ratedCDRDao = new RatedCDRDao();
        List<RatedCDR> ratedCDRs = ratedCDRDao.getAllRatedCDRs();
        
        if (ratedCDRs.isEmpty()) {
            System.out.println("No rated CDRs found to transfer.");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Create invoice for each customer
                for (RatedCDR cdr : ratedCDRs) {
                    // Get customer ID from phone number
                    Long customerId = getCustomerIdByPhoneNumber(conn, cdr.getDialA());
                    if (customerId == null) {
                        System.err.println("Customer not found for phone number: " + cdr.getDialA());
                        continue;
                    }
                    
                    // Create or get existing invoice
                    Long invoiceId = getOrCreateInvoice(conn, customerId);
                    
                    // Insert customer invoice record
                    insertCustomerInvoice(conn, invoiceId, customerId, cdr);
                }
                
                conn.commit();
                System.out.println("Successfully transferred " + ratedCDRs.size() + " rated CDRs to invoices.");
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    
    private static Long getCustomerIdByPhoneNumber(Connection conn, String phoneNumber) throws SQLException {
        String query = "SELECT id FROM customers WHERE phone_number = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        
        return null;
    }
    
    private static Long getOrCreateInvoice(Connection conn, Long customerId) throws SQLException {
        // Check for existing invoice for today
        String checkQuery = """
            SELECT id FROM invoices 
            WHERE customer_id = ? AND DATE(created_at) = CURDATE()
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        
        // Create new invoice if none exists
        String insertQuery = """
            INSERT INTO invoices (customer_id, created_at, total_amount, status)
            VALUES (?, ?, 0.00, 'PENDING')
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, customerId);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        
        throw new SQLException("Failed to create invoice");
    }
    
    private static void insertCustomerInvoice(Connection conn, Long invoiceId, Long customerId, RatedCDR cdr) throws SQLException {
        String query = """
            INSERT INTO customer_invoices 
            (invoice_id, customer_id, service_type, usage_amount, charge_amount, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, invoiceId);
            stmt.setLong(2, customerId);
            stmt.setString(3, cdr.getServiceType());
            stmt.setInt(4, cdr.getVolume());
            stmt.setBigDecimal(5, cdr.getTotal());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            
            // Update invoice total
            updateInvoiceTotal(conn, invoiceId);
        }
    }
    
    private static void updateInvoiceTotal(Connection conn, Long invoiceId) throws SQLException {
        String query = """
            UPDATE invoices i
            SET total_amount = (
                SELECT COALESCE(SUM(charge_amount), 0)
                FROM customer_invoices
                WHERE invoice_id = ?
            )
            WHERE id = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, invoiceId);
            stmt.setLong(2, invoiceId);
            stmt.executeUpdate();
        }
    }
}
