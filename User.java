package acss.model;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private String staffId;
    private String password;
    private String email;
    private String contactNumber;
    private String assignedBranch;
    private UserRole role;

    public User(String fullName, String staffId, String password, String email, String contactNumber, String assignedBranch, UserRole role) {
        this.fullName = fullName;
        this.staffId = staffId;
        this.password = password;
        this.email = email;
        this.contactNumber = contactNumber;
        this.assignedBranch = assignedBranch;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public String getStaffId() { return staffId; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getAssignedBranch() { return assignedBranch; }
    public UserRole getRole() { return role; }

    public void setPassword(String password) { this.password = password; }
    public void setRole(UserRole role) { this.role = role; }
    public void setAssignedBranch(String assignedBranch) { this.assignedBranch = assignedBranch; }
} 