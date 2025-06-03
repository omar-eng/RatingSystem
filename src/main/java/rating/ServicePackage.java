package rating;

import java.math.BigDecimal;

/**
 * Service Package model class
 */
public class ServicePackage {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long ratePlanId;
    private Boolean isRecurring;
    private Integer freeUnits;
    
    // Constructors
    public ServicePackage() {}
    
    public ServicePackage(String name, String description, BigDecimal price, 
                         Long ratePlanId, Boolean isRecurring, Integer freeUnits) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.ratePlanId = ratePlanId;
        this.isRecurring = isRecurring;
        this.freeUnits = freeUnits;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Long getRatePlanId() { return ratePlanId; }
    public void setRatePlanId(Long ratePlanId) { this.ratePlanId = ratePlanId; }
    
    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }
    
    public Integer getFreeUnits() { return freeUnits; }
    public void setFreeUnits(Integer freeUnits) { this.freeUnits = freeUnits; }
    
    @Override
    public String toString() {
        return String.format("ServicePackage{id=%d, name='%s', price=%s, freeUnits=%d}",
                id, name, price, freeUnits);
    }
}