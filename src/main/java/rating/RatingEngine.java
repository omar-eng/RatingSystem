package rating;

import db.dao.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CDR Rating Engine - processes CDRs and calculates charges
 */
public class RatingEngine {
    
    private final CDRDao cdrDao;
    private final CustomerDao customerDao;
    private final ServiceDao serviceDao;
    private final RatedCDRDao ratedCDRDao;
    
    public RatingEngine() {
        this.cdrDao = new CDRDao();
        this.customerDao = new CustomerDao();
        this.serviceDao = new ServiceDao();
        this.ratedCDRDao = new RatedCDRDao();
    }
    
    /**
     * Process all unrated CDRs
     */
    public void processUnratedCDRs() throws SQLException {
        System.out.println("Starting CDR rating process...");
        
        List<CDR> unratedCDRs = cdrDao.getUnratedCDRs();
        System.out.println("Found " + unratedCDRs.size() + " unrated CDRs to process");
        
        if (unratedCDRs.isEmpty()) {
            System.out.println("No unrated CDRs found. Process completed.");
            return;
        }
        
        List<RatedCDR> ratedCDRs = new ArrayList<>();
        int processedCount = 0;
        int errorCount = 0;
        
        for (CDR cdr : unratedCDRs) {
            try {
                RatedCDR ratedCDR = rateCDR(cdr);
                if (ratedCDR != null) {
                    ratedCDRs.add(ratedCDR);
                    processedCount++;
                } else {
                    System.err.println("Failed to rate CDR: " + cdr);
                    errorCount++;
                }
            } catch (Exception e) {
                System.err.println("Error rating CDR " + cdr.getId() + ": " + e.getMessage());
                errorCount++;
            }
        }
        
        // Batch insert rated CDRs
        if (!ratedCDRs.isEmpty()) {
            ratedCDRDao.insertRatedCDRsBatch(ratedCDRs);
        }
        
        System.out.println("CDR rating process completed:");
        System.out.println("- Successfully processed: " + processedCount + " CDRs");
        System.out.println("- Errors: " + errorCount + " CDRs");
    }
    
    /**
     * Rate a single CDR
     */
    public RatedCDR rateCDR(CDR cdr) throws SQLException {
        // Get customer information
        Customer customer = customerDao.getCustomerByPhoneNumber(cdr.getDialA());
        if (customer == null) {
            System.err.println("Customer not found for phone number: " + cdr.getDialA());
            return null;
        }
        
        // Get customer's rate plan and service package
        ServicePackage servicePackage = serviceDao.getServicePackageByTypeAndRatePlan(
            cdr.getServiceType(), customer.getRatePlanId());
        
        if (servicePackage == null) {
            System.err.println("Service package not found for service type: " + cdr.getServiceType() + 
                             " and rate plan: " + customer.getRatePlanId());
            return null;
        }
        
        // Get the specific service for pricing
        Service service = serviceDao.getServiceByTypeAndPackage(cdr.getServiceType(), servicePackage.getId());
        if (service == null) {
            System.err.println("Service not found for type: " + cdr.getServiceType() + 
                             " and package: " + servicePackage.getId());
            return null;
        }
        
        // Calculate charge
        BigDecimal totalCharge = calculateCharge(cdr, service, servicePackage);
        
        // Create rated CDR
        RatedCDR ratedCDR = new RatedCDR();
        ratedCDR.setDialA(cdr.getDialA());
        ratedCDR.setDialB(cdr.getDialB());
        ratedCDR.setServiceType(mapServiceType(cdr.getServiceType()));
        ratedCDR.setVolume(cdr.getUsage().intValue());
        ratedCDR.setStartTime(cdr.getStartTime());
        ratedCDR.setTotal(totalCharge);
        
        System.out.println("Rated CDR - Customer: " + cdr.getDialA() + 
                          ", Service: " + cdr.getServiceType() + 
                          ", Usage: " + cdr.getUsage() + 
                          ", Charge: " + totalCharge);
        
        return ratedCDR;
    }
    
    /**
     * Calculate charge for a CDR based on service and package
     */
    private BigDecimal calculateCharge(CDR cdr, Service service, ServicePackage servicePackage) {
        BigDecimal usage = BigDecimal.valueOf(cdr.getUsage());
        BigDecimal unitPrice = service.getUnitPrice();
        
        // Basic calculation: usage * unit_price
        BigDecimal totalCharge = usage.multiply(unitPrice);
        
        // Apply any package-specific logic (e.g., free units)
        Integer freeUnits = servicePackage.getFreeUnits();
        if (freeUnits != null && freeUnits > 0) {
            BigDecimal freeUnitsBD = BigDecimal.valueOf(freeUnits);
            if (usage.compareTo(freeUnitsBD) <= 0) {
                // Usage is within free units
                totalCharge = BigDecimal.ZERO;
            } else {
                // Charge only for usage above free units
                BigDecimal chargeableUsage = usage.subtract(freeUnitsBD);
                totalCharge = chargeableUsage.multiply(unitPrice);
            }
        }
        
        // Round to 2 decimal places
        return totalCharge.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Map service type from CDR format to rated_cdr format
     */
    private String mapServiceType(String serviceType) {
        switch (serviceType.toUpperCase()) {
            case "VOICE":
                return "voice";
            case "DATA":
                return "data";
            case "SMS":
                return "sms";
            default:
                return serviceType.toLowerCase();
        }
    }
    
    /**
     * Get rating statistics
     */
    public void printRatingStatistics() throws SQLException {
        List<RatedCDR> allRatedCDRs = ratedCDRDao.getAllRatedCDRs();
        
        System.out.println("\n=== Rating Statistics ===");
        System.out.println("Total rated CDRs: " + allRatedCDRs.size());
        
        // Group by service type
        long voiceCount = allRatedCDRs.stream().filter(r -> "voice".equals(r.getServiceType())).count();
        long dataCount = allRatedCDRs.stream().filter(r -> "data".equals(r.getServiceType())).count();
        long smsCount = allRatedCDRs.stream().filter(r -> "sms".equals(r.getServiceType())).count();
        
        System.out.println("Voice CDRs: " + voiceCount);
        System.out.println("Data CDRs: " + dataCount);
        System.out.println("SMS CDRs: " + smsCount);
        
        // Calculate total charges
        BigDecimal totalCharges = allRatedCDRs.stream()
            .map(RatedCDR::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Total charges: $" + totalCharges);
        System.out.println("========================\n");
    }
}