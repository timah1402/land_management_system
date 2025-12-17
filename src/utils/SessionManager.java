package utils;

import models.User;

/**
 * Singleton class to manage user session (logged-in user)
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private String sessionToken;
    private long loginTime;

    // Private constructor for Singleton pattern
    private SessionManager() {
    }

    /**
     * Get the singleton instance
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Start a new session for a user
     * @param user The user logging in
     */
    public void startSession(User user) {
        this.currentUser = user;
        this.sessionToken = generateSessionToken();
        this.loginTime = System.currentTimeMillis();
        System.out.println("Session started for user: " + user.getEmail());
    }

    /**
     * End the current session
     */
    public void endSession() {
        if (currentUser != null) {
            System.out.println("Session ended for user: " + currentUser.getEmail());
        }
        this.currentUser = null;
        this.sessionToken = null;
        this.loginTime = 0;
    }

    /**
     * Check if a user is logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Get the current logged-in user
     * @return The current user, or null if no one is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get the current user's ID
     * @return User ID, or -1 if no one is logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    /**
     * Get the current user's role
     * @return User role, or null if no one is logged in
     */
    public User.UserRole getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    /**
     * Get the current user's full name
     * @return User's full name, or "Guest" if no one is logged in
     */
    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : "Guest";
    }

    /**
     * Get the session token
     * @return Session token
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Get login time
     * @return Login timestamp
     */
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * Get session duration in minutes
     * @return Session duration in minutes
     */
    public long getSessionDurationMinutes() {
        if (loginTime == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - loginTime) / (1000 * 60);
    }

    /**
     * Check if user is Admin
     * @return true if current user is admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.UserRole.ADMIN;
    }

    /**
     * Check if user is Agent
     * @return true if current user is agent, false otherwise
     */
    public boolean isAgent() {
        return currentUser != null && currentUser.getRole() == User.UserRole.AGENT;
    }

    /**
     * Check if user is Citizen
     * @return true if current user is citizen, false otherwise
     */
    public boolean isCitizen() {
        return currentUser != null && currentUser.getRole() == User.UserRole.CITIZEN;
    }

    /**
     * Update current user information
     * @param user Updated user object
     */
    public void updateCurrentUser(User user) {
        if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
            this.currentUser = user;
        }
    }

    /**
     * Check if session has expired (default 8 hours)
     * @return true if session has expired, false otherwise
     */
    public boolean isSessionExpired() {
        if (loginTime == 0) {
            return true;
        }
        long sessionDurationHours = (System.currentTimeMillis() - loginTime) / (1000 * 60 * 60);
        return sessionDurationHours >= 8;
    }

    /**
     * Refresh session (update login time)
     */
    public void refreshSession() {
        this.loginTime = System.currentTimeMillis();
    }

    /**
     * Generate a simple session token
     * @return Session token string
     */
    private String generateSessionToken() {
        return "SESSION_" + System.currentTimeMillis() + "_" +
                (int)(Math.random() * 100000);
    }

    /**
     * Get session info as string
     * @return Session information
     */
    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "No active session";
        }

        return String.format("User: %s | Role: %s | Duration: %d minutes",
                currentUser.getFullName(),
                currentUser.getRole(),
                getSessionDurationMinutes());
    }
}