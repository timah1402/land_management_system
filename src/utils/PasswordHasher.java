package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordHasher {

    // Number of rounds for BCrypt (10-12 is recommended)
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hash a plain text password
     * @param plainPassword The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a plain text password against a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Check if a password needs rehashing (if BCrypt rounds have changed)
     * @param hashedPassword The hashed password to check
     * @return true if password needs rehashing, false otherwise
     */
    public static boolean needsRehash(String hashedPassword) {
        // Extract the number of rounds from the hash
        // BCrypt hash format: $2a$rounds$salt+hash
        try {
            String[] parts = hashedPassword.split("\\$");
            if (parts.length >= 3) {
                int rounds = Integer.parseInt(parts[2]);
                return rounds != BCRYPT_ROUNDS;
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    /**
     * Generate a random password (for testing or temporary passwords)
     * @param length The length of the password to generate
     * @return A random password
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }

    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        // Password must have at least 3 of the 4 categories
        int categories = 0;
        if (hasUpper) categories++;
        if (hasLower) categories++;
        if (hasDigit) categories++;
        if (hasSpecial) categories++;

        return categories >= 3;
    }

    /**
     * Get password strength description
     * @param password The password to evaluate
     * @return A string describing password strength
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Empty";
        }

        if (password.length() < 6) {
            return "Very Weak";
        }

        if (password.length() < 8) {
            return "Weak";
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int score = 0;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        if (password.length() >= 12) score++;

        if (score <= 2) return "Weak";
        if (score == 3) return "Medium";
        if (score == 4) return "Strong";
        return "Very Strong";
    }
}