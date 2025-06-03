package rating;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Rated CDR model class representing processed CDRs with charges
 */
public class RatedCDR {
    private Integer id;
    private String dialA;
    private String dialB;
    private String serviceType;
    private Integer volume;
    private LocalDateTime startTime;
    private BigDecimal total;
    
    // Constructors
    public RatedCDR() {}
    
    public RatedCDR(String dialA, String dialB, String serviceType, 
                   Integer volume, LocalDateTime startTime, BigDecimal total) {
        this.dialA = dialA;
        this.dialB = dialB;
        this.serviceType = serviceType;
        this.volume = volume;
        this.startTime = startTime;
        this.total = total;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getDialA() { return dialA; }
    public void setDialA(String dialA) { this.dialA = dialA; }
    
    public String getDialB() { return dialB; }
    public void setDialB(String dialB) { this.dialB = dialB; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public Integer getVolume() { return volume; }
    public void setVolume(Integer volume) { this.volume = volume; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    @Override
    public String toString() {
        return String.format("RatedCDR{id=%d, dialA='%s', serviceType='%s', volume=%d, total=%s}",
                id, dialA, serviceType, volume, total);
    }
}