package db.dao;

import db.DatabaseConnection;
import rating.RatedCDR;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Rated CDR operations
 */
public class RatedCDRDao {
    
    /**
     * Insert a rated CDR
     */
    public void insertRatedCDR(RatedCDR ratedCDR) throws SQLException {
        String query = """
            INSERT INTO rated_cdrs (dial_a, dial_b, service_type, volume, start_time, total)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, ratedCDR.getDialA());
            stmt.setString(2, ratedCDR.getDialB());
            stmt.setString(3, ratedCDR.getServiceType());
            stmt.setInt(4, ratedCDR.getVolume());
            stmt.setTimestamp(5, Timestamp.valueOf(ratedCDR.getStartTime()));
            stmt.setBigDecimal(6, ratedCDR.getTotal());
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Insert multiple rated CDRs in batch
     */
    public void insertRatedCDRsBatch(List<RatedCDR> ratedCDRs) throws SQLException {
        String query = """
            INSERT INTO rated_cdrs (dial_a, dial_b, service_type, volume, start_time, total)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            
            for (RatedCDR ratedCDR : ratedCDRs) {
                stmt.setString(1, ratedCDR.getDialA());
                stmt.setString(2, ratedCDR.getDialB());
                stmt.setString(3, ratedCDR.getServiceType());
                stmt.setInt(4, ratedCDR.getVolume());
                stmt.setTimestamp(5, Timestamp.valueOf(ratedCDR.getStartTime()));
                stmt.setBigDecimal(6, ratedCDR.getTotal());
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit();
            
            System.out.println("Batch inserted " + ratedCDRs.size() + " rated CDRs");
            
        } catch (SQLException e) {
            System.err.println("Error during batch insert: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get all rated CDRs
     */
    public List<RatedCDR> getAllRatedCDRs() throws SQLException {
        List<RatedCDR> ratedCDRs = new ArrayList<>();
        String query = "SELECT * FROM rated_cdrs ORDER BY start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                RatedCDR ratedCDR = new RatedCDR();
                ratedCDR.setId(rs.getInt("id"));
                ratedCDR.setDialA(rs.getString("dial_a"));
                ratedCDR.setDialB(rs.getString("dial_b"));
                ratedCDR.setServiceType(rs.getString("service_type"));
                ratedCDR.setVolume(rs.getInt("volume"));
                
                Timestamp timestamp = rs.getTimestamp("start_time");
                if (timestamp != null) {
                    ratedCDR.setStartTime(timestamp.toLocalDateTime());
                }
                
                ratedCDR.setTotal(rs.getBigDecimal("total"));
                
                ratedCDRs.add(ratedCDR);
            }
        }
        
        return ratedCDRs;
    }
    
    /**
     * Get rated CDRs by customer phone number
     */
    public List<RatedCDR> getRatedCDRsByCustomer(String dialA) throws SQLException {
        List<RatedCDR> ratedCDRs = new ArrayList<>();
        String query = "SELECT * FROM rated_cdrs WHERE dial_a = ? ORDER BY start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, dialA);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RatedCDR ratedCDR = new RatedCDR();
                    ratedCDR.setId(rs.getInt("id"));
                    ratedCDR.setDialA(rs.getString("dial_a"));
                    ratedCDR.setDialB(rs.getString("dial_b"));
                    ratedCDR.setServiceType(rs.getString("service_type"));
                    ratedCDR.setVolume(rs.getInt("volume"));
                    
                    Timestamp timestamp = rs.getTimestamp("start_time");
                    if (timestamp != null) {
                        ratedCDR.setStartTime(timestamp.toLocalDateTime());
                    }
                    
                    ratedCDR.setTotal(rs.getBigDecimal("total"));
                    
                    ratedCDRs.add(ratedCDR);
                }
            }
        }
        
        return ratedCDRs;
    }
    
    /**
     * Clear all rated CDRs (for testing purposes)
     */
    public void clearAllRatedCDRs() throws SQLException {
        String query = "DELETE FROM rated_cdrs";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            int deletedRows = stmt.executeUpdate();
            System.out.println("Cleared " + deletedRows + " rated CDRs from database");
        }
    }
    
    public void createRatedCDRTable() throws SQLException {
        String query = """
            CREATE TABLE IF NOT EXISTS rated_cdrs (
                id INT PRIMARY KEY AUTO_INCREMENT,
                dial_a VARCHAR(20) NOT NULL,
                dial_b VARCHAR(20),
                service_type VARCHAR(20) NOT NULL,
                volume INT NOT NULL,
                start_time DATETIME NOT NULL,
                total DECIMAL(10,2) NOT NULL
            )
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        }
    }
}