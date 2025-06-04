package rating;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * CDR (Call Detail Record) model class
 */
public class CDR {
    private Long id;
    private String dialA;
    private String dialB;
    private String serviceType;
    private Long usage;
    private LocalDateTime startTime;
    private Double externalCharges;
    private Long customerId;
    private Long invoiceId;
    
    // Constructors
    public CDR() {}
    
    public CDR(String dialA, String dialB, String serviceType, Long usage, 
               LocalDateTime startTime, Double externalCharges, Long customerId) {
        this.dialA = dialA;
        this.dialB = dialB;
        this.serviceType = serviceType;
        this.usage = usage;
        this.startTime = startTime;
        this.externalCharges = externalCharges;
        this.customerId = customerId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDialA() { return dialA; }
    public void setDialA(String dialA) { this.dialA = dialA; }
    
    public String getDialB() { return dialB; }
    public void setDialB(String dialB) { this.dialB = dialB; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public Long getUsage() { return usage; }
    public void setUsage(Long usage) { this.usage = usage; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public Double getExternalCharges() { return externalCharges; }
    public void setExternalCharges(Double externalCharges) { this.externalCharges = externalCharges; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    
    @Override
    public String toString() {
        return String.format("CDR{id=%d, dialA='%s', dialB='%s', serviceType='%s', usage=%d, startTime=%s, customerId=%d}",
                id, dialA, dialB, serviceType, usage, startTime, customerId);
    }
}
