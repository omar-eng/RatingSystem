package db.dao;

import db.DatabaseConnection;
import rating.Service;
import rating.ServicePackage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Service operations
 */
public class ServiceDao {
    
    /**
     * Get service by type and service package ID
     */
    public Service getServiceByTypeAndPackage(String serviceType, Long servicePackageId) throws SQLException {
        String query = "SELECT * FROM services WHERE type = ? AND service_package_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, serviceType);
            stmt.setLong(2, servicePackageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getLong("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setType(rs.getString("type"));
                    service.setUnitPrice(rs.getBigDecimal("unit_price"));
                    service.setUnitType(rs.getString("unit_type"));
                    
                    long packageId = rs.getLong("service_package_id");
                    if (!rs.wasNull()) {
                        service.setServicePackageId(packageId);
                    }
                    
                    return service;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get all services by service package ID
     */
    public List<Service> getServicesByPackageId(Long servicePackageId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services WHERE service_package_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, servicePackageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getLong("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setType(rs.getString("type"));
                    service.setUnitPrice(rs.getBigDecimal("unit_price"));
                    service.setUnitType(rs.getString("unit_type"));
                    service.setServicePackageId(rs.getLong("service_package_id"));
                    
                    services.add(service);
                }
            }
        }
        
        return services;
    }
    
    /**
     * Get service packages by rate plan ID
     */
    public List<ServicePackage> getServicePackagesByRatePlanId(Long ratePlanId) throws SQLException {
        List<ServicePackage> packages = new ArrayList<>();
        String query = "SELECT * FROM service_packages WHERE rate_plan_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, ratePlanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ServicePackage servicePackage = new ServicePackage();
                    servicePackage.setId(rs.getLong("id"));
                    servicePackage.setName(rs.getString("name"));
                    servicePackage.setDescription(rs.getString("description"));
                    servicePackage.setPrice(rs.getBigDecimal("price"));
                    servicePackage.setRatePlanId(rs.getLong("rate_plan_id"));
                    servicePackage.setIsRecurring(rs.getBoolean("is_recurring"));
                    servicePackage.setFreeUnits(rs.getInt("free_units"));
                    
                    packages.add(servicePackage);
                }
            }
        }
        
        return packages;
    }
    
    /**
     * Get service package by service type and rate plan ID
     */
    public ServicePackage getServicePackageByTypeAndRatePlan(String serviceType, Long ratePlanId) throws SQLException {
        String query = """
            SELECT sp.* FROM service_packages sp
            JOIN services s ON sp.id = s.service_package_id
            WHERE s.type = ? AND sp.rate_plan_id = ?
            LIMIT 1
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, serviceType);
            stmt.setLong(2, ratePlanId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ServicePackage servicePackage = new ServicePackage();
                    servicePackage.setId(rs.getLong("id"));
                    servicePackage.setName(rs.getString("name"));
                    servicePackage.setDescription(rs.getString("description"));
                    servicePackage.setPrice(rs.getBigDecimal("price"));
                    servicePackage.setRatePlanId(rs.getLong("rate_plan_id"));
                    servicePackage.setIsRecurring(rs.getBoolean("is_recurring"));
                    servicePackage.setFreeUnits(rs.getInt("free_units"));
                    
                    return servicePackage;
                }
            }
        }
        
        return null;
    }
}