package rating;

/**
 * Customer model class
 */
public class Customer {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private Long ratePlanId;
    
    // Constructors
    public Customer() {}
    
    public Customer(String name, String phoneNumber, String email, String address, Long ratePlanId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.ratePlanId = ratePlanId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Long getRatePlanId() { return ratePlanId; }
    public void setRatePlanId(Long ratePlanId) { this.ratePlanId = ratePlanId; }
    
    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s', phoneNumber='%s', ratePlanId=%d}",
                id, name, phoneNumber, ratePlanId);
    }
}