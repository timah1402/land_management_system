package models;

/**
 * Admin class representing system administrators
 */
public class Admin extends User {

    // Attributes
    private int adminId;
    private AccessLevel accessLevel;
    private String department;

    // Enum
    public enum AccessLevel {
        FULL, LIMITED
    }

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String lastName, String firstName, String email, String phone,
                 String password, AccessLevel accessLevel, String department) {
        super(lastName, firstName, email, phone, password, UserRole.ADMIN);
        this.accessLevel = accessLevel;
        this.department = department;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", accessLevel=" + accessLevel +
                ", department='" + department + '\'' +
                '}';
    }
}