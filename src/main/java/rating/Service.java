package rating;

import java.math.BigDecimal;

/**
 * Service model class
 */
public class Service {
    private Long id;
    private String name;
    private String description;
    private String type;
    private BigDecimal unitPrice;
    private String unitType;
    private Long servicePackageId;
    
    // Constructors
    public Service() {}
    
    public Service(String name, String description, String type, 
                  BigDecimal unitPrice, String unitType, Long servicePackageId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.unitPrice = unitPrice;
        this.unitType = unitType;
        this.servicePackageId = servicePackageId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }
    
    public Long getServicePackageId() { return servicePackageId; }
    public void setServicePackageId(Long servicePackageId) { this.servicePackageId = servicePackageId; }
    
    @Override
    public String toString() {
        return String.format("Service{id=%d, name='%s', type='%s', unitPrice=%s, unitType='%s'}",
                id, name, type, unitPrice, unitType);
    }
}