package models;

import java.sql.Timestamp;

/**
 * Base User class representing all users in the system
 */
public class User {

    // Attributes
    private int userId;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private String password;
    private UserRole role;
    private AccountStatus accountStatus;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    // Enums
    public enum UserRole {
        ADMIN, AGENT, CITIZEN
    }

    public enum AccountStatus {
        PENDING, ACTIVE, SUSPENDED, REJECTED
    }

    // Constructors
    public User() {
    }

    public User(String lastName, String firstName, String email, String phone,
                String password, UserRole role) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.accountStatus = AccountStatus.PENDING;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", status=" + accountStatus +
                '}';
    }
}