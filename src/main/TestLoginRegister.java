package main;

import database.DatabaseConfig;
import database.DatabaseInitializer;
import dao.CitizenDAO;
import dao.UserDAO;
import dao.AdminDAO;
import dao.LandAgentDAO;
import models.Citizen;
import models.User;
import models.Admin;
import models.LandAgent;
import utils.PasswordHasher;
import views.LoginFrame;

import java.sql.Date;

import java.sql.Date;

import javax.swing.*;
import java.sql.Date;

/**
 * Test class for Login/Register system
 */
public class TestLoginRegister {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  TESTING LOGIN/REGISTER SYSTEM");
        System.out.println("==============================================\n");

        // Step 1: Test database connection
        System.out.println("Step 1: Testing Database Connection...");
        if (!testDatabaseConnection()) {
            System.err.println("✗ Database connection failed. Exiting.");
            return;
        }
        System.out.println("✓ Database connection successful\n");

        // Step 2: Create test data
        System.out.println("Step 2: Creating Test Users...");
        createTestUsers();
        System.out.println();

        // Step 3: Test password hashing
        System.out.println("Step 3: Testing Password Hashing...");
        testPasswordHashing();
        System.out.println();

        // Step 4: Test login functionality
        System.out.println("Step 4: Testing Login Functionality...");
        testLogin();
        System.out.println();

        // Step 5: Launch UI
        System.out.println("Step 5: Launching Login UI...");
        System.out.println("==============================================");
        System.out.println("Test users created:");
        System.out.println("ADMIN: admin@system.com | Password: Admin123!");
        System.out.println("AGENT: agent@dakar.com | Password: Agent123!");
        System.out.println("1. Email: test@citizen.com | Password: Test1234!");
        System.out.println("2. Email: john.doe@email.com | Password: Pass1234!");
        System.out.println("==============================================\n");

        launchLoginUI();
    }

    private static boolean testDatabaseConnection() {
        try {
            boolean connected = DatabaseConfig.testConnection();
            if (connected) {
                System.out.println("✓ Database connection established");
                return true;
            } else {
                System.err.println("✗ Cannot connect to database");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void createTestUsers() {
        CitizenDAO citizenDAO = new CitizenDAO();
        UserDAO userDAO = new UserDAO();
        AdminDAO adminDAO = new AdminDAO();
        LandAgentDAO agentDAO = new LandAgentDAO();

        try {
            // Test Admin User
            String adminEmail = "admin@system.com";
            if (!userDAO.emailExists(adminEmail)) {
                Admin admin = new Admin();
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setEmail(adminEmail);
                admin.setPhone("771111111");
                admin.setPassword(PasswordHasher.hashPassword("Admin123!"));
                admin.setRole(User.UserRole.ADMIN);
                admin.setAccountStatus(User.AccountStatus.ACTIVE);
                admin.setAccessLevel(Admin.AccessLevel.FULL);
                admin.setDepartment("IT");

                if (adminDAO.createAdmin(admin)) {
                    System.out.println("✓ Admin user created: " + adminEmail);
                } else {
                    System.out.println("✗ Failed to create admin user");
                }
            } else {
                System.out.println("ℹ Admin user already exists: " + adminEmail);
            }

            // Test Agent User
            String agentEmail = "agent@dakar.com";
            if (!userDAO.emailExists(agentEmail)) {
                LandAgent agent = new LandAgent();
                agent.setFirstName("Mamadou");
                agent.setLastName("Diop");
                agent.setEmail(agentEmail);
                agent.setPhone("772222222");
                agent.setPassword(PasswordHasher.hashPassword("Agent123!"));
                agent.setRole(User.UserRole.AGENT);
                agent.setAccountStatus(User.AccountStatus.ACTIVE);
                agent.setRegistrationNumber("MAT-2024-001");
                agent.setRegion("Dakar");
                agent.setSpecialization("Urban Land");
                agent.setAppointmentDate(Date.valueOf("2024-01-01"));
                agent.setStatus(LandAgent.AgentStatus.ACTIVE);

                if (agentDAO.createLandAgent(agent)) {
                    System.out.println("✓ Agent user created: " + agentEmail);
                } else {
                    System.out.println("✗ Failed to create agent user");
                }
            } else {
                System.out.println("ℹ Agent user already exists: " + agentEmail);
            }

            // Test User 1
            String testEmail1 = "test@citizen.com";
            if (!userDAO.emailExists(testEmail1)) {
                Citizen citizen1 = new Citizen();
                citizen1.setFirstName("Test");
                citizen1.setLastName("Citizen");
                citizen1.setEmail(testEmail1);
                citizen1.setPhone("771234567");
                citizen1.setPassword(PasswordHasher.hashPassword("Test1234!"));
                citizen1.setRole(User.UserRole.CITIZEN);
                citizen1.setAccountStatus(User.AccountStatus.ACTIVE); // Set as ACTIVE for testing
                citizen1.setIdCardNumber("1234567890123");
                citizen1.setDateOfBirth(Date.valueOf("1990-01-01"));
                citizen1.setPlaceOfBirth("Dakar");
                citizen1.setFullAddress("123 Test Street, Dakar");
                citizen1.setOccupation("Software Developer");

                if (citizenDAO.createCitizen(citizen1)) {
                    System.out.println("✓ Test user 1 created: " + testEmail1);
                } else {
                    System.out.println("✗ Failed to create test user 1");
                }
            } else {
                System.out.println("ℹ Test user 1 already exists: " + testEmail1);
            }

            // Test User 2
            String testEmail2 = "john.doe@email.com";
            if (!userDAO.emailExists(testEmail2)) {
                Citizen citizen2 = new Citizen();
                citizen2.setFirstName("John");
                citizen2.setLastName("Doe");
                citizen2.setEmail(testEmail2);
                citizen2.setPhone("779876543");
                citizen2.setPassword(PasswordHasher.hashPassword("Pass1234!"));
                citizen2.setRole(User.UserRole.CITIZEN);
                citizen2.setAccountStatus(User.AccountStatus.ACTIVE);
                citizen2.setIdCardNumber("9876543210123");
                citizen2.setDateOfBirth(Date.valueOf("1985-05-15"));
                citizen2.setPlaceOfBirth("Thiès");
                citizen2.setFullAddress("456 Main Avenue, Thiès");
                citizen2.setOccupation("Teacher");

                if (citizenDAO.createCitizen(citizen2)) {
                    System.out.println("✓ Test user 2 created: " + testEmail2);
                } else {
                    System.out.println("✗ Failed to create test user 2");
                }
            } else {
                System.out.println("ℹ Test user 2 already exists: " + testEmail2);
            }

            // Test User 3 - PENDING status
            String testEmail3 = "pending@test.com";
            if (!userDAO.emailExists(testEmail3)) {
                Citizen citizen3 = new Citizen();
                citizen3.setFirstName("Pending");
                citizen3.setLastName("User");
                citizen3.setEmail(testEmail3);
                citizen3.setPhone("776543210");
                citizen3.setPassword(PasswordHasher.hashPassword("Pending123!"));
                citizen3.setRole(User.UserRole.CITIZEN);
                citizen3.setAccountStatus(User.AccountStatus.PENDING);
                citizen3.setIdCardNumber("1111111111111");
                citizen3.setDateOfBirth(Date.valueOf("1995-03-20"));
                citizen3.setPlaceOfBirth("Dakar");
                citizen3.setFullAddress("789 Pending Street, Dakar");
                citizen3.setOccupation("Student");

                if (citizenDAO.createCitizen(citizen3)) {
                    System.out.println("✓ Test user 3 created (PENDING): " + testEmail3);
                } else {
                    System.out.println("✗ Failed to create test user 3");
                }
            } else {
                System.out.println("ℹ Test user 3 already exists: " + testEmail3);
            }

        } catch (Exception e) {
            System.err.println("✗ Error creating test users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testPasswordHashing() {
        String plainPassword = "MySecurePassword123!";

        try {
            // Test hashing
            String hashedPassword = PasswordHasher.hashPassword(plainPassword);
            System.out.println("✓ Password hashing works");
            System.out.println("  Plain: " + plainPassword);
            System.out.println("  Hash: " + hashedPassword.substring(0, 30) + "...");

            // Test verification
            boolean verified = PasswordHasher.verifyPassword(plainPassword, hashedPassword);
            if (verified) {
                System.out.println("✓ Password verification works");
            } else {
                System.out.println("✗ Password verification failed");
            }

            // Test wrong password
            boolean wrongVerify = PasswordHasher.verifyPassword("WrongPassword", hashedPassword);
            if (!wrongVerify) {
                System.out.println("✓ Wrong password correctly rejected");
            } else {
                System.out.println("✗ Wrong password was accepted!");
            }

            // Test password strength
            System.out.println("✓ Password strength: " + PasswordHasher.getPasswordStrength(plainPassword));

        } catch (Exception e) {
            System.err.println("✗ Password hashing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testLogin() {
        UserDAO userDAO = new UserDAO();

        try {
            // Test valid login
            String email = "test@citizen.com";
            String password = "Test1234!";

            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                System.out.println("✓ User found: " + user.getFullName());

                boolean passwordMatch = PasswordHasher.verifyPassword(password, user.getPassword());
                if (passwordMatch) {
                    System.out.println("✓ Password verification successful");
                    System.out.println("✓ Account status: " + user.getAccountStatus());
                    System.out.println("✓ User role: " + user.getRole());
                } else {
                    System.out.println("✗ Password verification failed");
                }
            } else {
                System.out.println("✗ User not found");
            }

            // Test invalid email
            User invalidUser = userDAO.getUserByEmail("nonexistent@test.com");
            if (invalidUser == null) {
                System.out.println("✓ Invalid email correctly returns null");
            } else {
                System.out.println("✗ Invalid email returned a user!");
            }

        } catch (Exception e) {
            System.err.println("✗ Login test error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void launchLoginUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and show login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);

                System.out.println("✓ Login UI launched successfully");
                System.out.println("\nYou can now:");
                System.out.println("1. Login with test credentials");
                System.out.println("2. Register a new account");
                System.out.println("3. Test with pending user: pending@test.com / Pending123!");

            } catch (Exception e) {
                System.err.println("✗ Error launching UI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}