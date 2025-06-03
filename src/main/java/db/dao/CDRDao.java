package db.dao;

import db.DatabaseConnection;
import rating.CDR;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CDR operations
 */
public class CDRDao {
    
    /**
     * Get all unrated CDRs (CDRs not yet processed)
     */
    public List<CDR> getUnratedCDRs() throws SQLException {
        List<CDR> cdrs = new ArrayList<>();
        String query = "SELECT * FROM cdrs WHERE invoice_id IS NULL ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                CDR cdr = new CDR();
                cdr.setId(rs.getLong("id"));
                cdr.setDialA(rs.getString("dial_a"));
                cdr.setDialB(rs.getString("dial_b"));
                cdr.setServiceType(rs.getString("service_type"));
                cdr.setUsage(rs.getLong("usage"));
                
                Timestamp timestamp = rs.getTimestamp("start_time");
                if (timestamp != null) {
                    cdr.setStartTime(timestamp.toLocalDateTime());
                }
                
                cdr.setExternalCharges(rs.getDouble("external_charges"));
                cdr.setCustomerId(rs.getLong("customer_id"));
                
                // Handle null invoice_id
                long invoiceId = rs.getLong("invoice_id");
                if (!rs.wasNull()) {
                    cdr.setInvoiceId(invoiceId);
                }
                
                cdrs.add(cdr);
            }
        }
        
        return cdrs;
    }
    
    /**
     * Get CDRs by customer ID
     */
    public List<CDR> getCDRsByCustomerId(Long customerId) throws SQLException {
        List<CDR> cdrs = new ArrayList<>();
        String query = "SELECT * FROM cdrs WHERE customer_id = ? ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CDR cdr = new CDR();
                    cdr.setId(rs.getLong("id"));
                    cdr.setDialA(rs.getString("dial_a"));
                    cdr.setDialB(rs.getString("dial_b"));
                    cdr.setServiceType(rs.getString("service_type"));
                    cdr.setUsage(rs.getLong("usage"));
                    
                    Timestamp timestamp = rs.getTimestamp("start_time");
                    if (timestamp != null) {
                        cdr.setStartTime(timestamp.toLocalDateTime());
                    }
                    
                    cdr.setExternalCharges(rs.getDouble("external_charges"));
                    cdr.setCustomerId(rs.getLong("customer_id"));
                    
                    long invoiceId = rs.getLong("invoice_id");
                    if (!rs.wasNull()) {
                        cdr.setInvoiceId(invoiceId);
                    }
                    
                    cdrs.add(cdr);
                }
            }
        }
        
        return cdrs;
    }
    
    /**
     * Insert sample CDR data for testing
     */
    public void insertSampleCDRs() throws SQLException {
        String query = """
            INSERT INTO cdrs (dial_a, dial_b, service_type, `usage`, start_time, external_charges, customer_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            
            // Sample CDRs
            Object[][] sampleData = {
                {"00201221234567", "00201221234568", "VOICE", 120L, LocalDateTime.now().minusHours(2), 0.0, 1L},
                {"00201221234567", "00201234567890", "VOICE", 300L, LocalDateTime.now().minusHours(1), 0.0, 1L},
                {"00201221234567", null, "DATA", 1024L, LocalDateTime.now().minusMinutes(30), 0.0, 1L},
                {"00201221234567", "00201987654321", "SMS", 5L, LocalDateTime.now().minusMinutes(15), 0.0, 1L},
                {"00201221234568", "00201221234567", "VOICE", 180L, LocalDateTime.now().minusHours(3), 0.0, 2L},
                {"00201221234568", null, "DATA", 2048L, LocalDateTime.now().minusHours(1), 0.0, 2L},
                {"00201221234569", "00201221234567", "VOICE", 90L, LocalDateTime.now().minusMinutes(45), 0.0, 3L},
                {"00201221234569", null, "DATA", 512L, LocalDateTime.now().minusMinutes(20), 0.0, 3L}
            };
            
            for (Object[] data : sampleData) {
                stmt.setString(1, (String) data[0]);
                stmt.setString(2, (String) data[1]);
                stmt.setString(3, (String) data[2]);
                stmt.setLong(4, (Long) data[3]);
                stmt.setTimestamp(5, Timestamp.valueOf((LocalDateTime) data[4]));
                stmt.setDouble(6, (Double) data[5]);
                stmt.setLong(7, (Long) data[6]);
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit();
            System.out.println("Sample CDR data inserted successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample CDR data: " + e.getMessage());
            throw e;
        }
    }
}