package org.example.school_bus_tracker_be.DTO;

import org.example.school_bus_tracker_be.Dtos.student.StudentResponse;
import java.util.List;

public class ParentWithStudentsResponse {
    private Long parentId;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private Long schoolId;
    private String schoolName;
    private List<StudentResponse> students;

    public ParentWithStudentsResponse() {}

    public ParentWithStudentsResponse(Long parentId, String parentName, String parentEmail, 
                                     String parentPhone, Long schoolId, String schoolName, 
                                     List<StudentResponse> students) {
        this.parentId = parentId;
        this.parentName = parentName;
        this.parentEmail = parentEmail;
        this.parentPhone = parentPhone;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.students = students;
    }

    // Getters and Setters
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public List<StudentResponse> getStudents() { return students; }
    public void setStudents(List<StudentResponse> students) { this.students = students; }
}
