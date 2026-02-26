package org.example.school_bus_tracker_be.DTO;

/**
 * Response after admin adds a parent. Parent has no password until they complete password reset.
 */
public class ParentResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String homeAddress;
    private Long schoolId;
    private String schoolName;

    public ParentResponse() {}

    public ParentResponse(Long id, String name, String email, String phone, Long schoolId, String schoolName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
    }

    public ParentResponse(Long id, String name, String email, String phone, String homeAddress, Long schoolId, String schoolName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homeAddress = homeAddress;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
}
