package org.example.school_bus_tracker_be.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for PUT /api/parent/profile (parent edits own profile).
 * All fields optional; only provided fields are updated.
 */
@Schema(description = "Parent profile update (edit own account)")
public class UpdateParentProfileRequest {

    @Schema(description = "Full name")
    private String name;

    @Schema(description = "Email (must be unique)")
    private String email;

    @Schema(description = "Phone (must be unique)")
    private String phone;

    @Schema(description = "Home address")
    private String homeAddress;

    @Schema(description = "School ID (optional)")
    private Long schoolId;

    @Schema(description = "New password (leave blank to keep current)")
    private String password;

    public UpdateParentProfileRequest() {}

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
