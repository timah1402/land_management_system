package utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+221|00221)?[0-9]{9}$" // Senegal phone format
    );

    private static final Pattern CNI_PATTERN = Pattern.compile(
            "^[0-9]{13}$" // Senegal CNI format (13 digits)
    );

    private static final Pattern PARCEL_NUMBER_PATTERN = Pattern.compile(
            "^[A-Z]{2,3}-[0-9]{4,6}$" // Example: DK-12345
    );

    private static final Pattern LAND_TITLE_PATTERN = Pattern.compile(
            "^TF[/-]?[0-9]{4,8}$" // Example: TF-12345678 or TF/12345678
    );

    /**
     * Validate email address
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number (Senegal format)
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove spaces and dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate CNI (Senegal ID card number)
     * @param cni CNI number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCNI(String cni) {
        if (cni == null || cni.trim().isEmpty()) {
            return false;
        }
        // Remove spaces and dashes
        String cleanCNI = cni.replaceAll("[\\s-]", "");
        return CNI_PATTERN.matcher(cleanCNI).matches();
    }

    /**
     * Validate parcel number
     * @param parcelNumber Parcel number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidParcelNumber(String parcelNumber) {
        if (parcelNumber == null || parcelNumber.trim().isEmpty()) {
            return false;
        }
        return PARCEL_NUMBER_PATTERN.matcher(parcelNumber.trim()).matches();
    }

    /**
     * Validate land title number
     * @param landTitle Land title to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLandTitle(String landTitle) {
        if (landTitle == null || landTitle.trim().isEmpty()) {
            return false;
        }
        return LAND_TITLE_PATTERN.matcher(landTitle.trim()).matches();
    }

    /**
     * Validate name (no numbers or special characters)
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmedName = name.trim();

        // Name must be at least 2 characters
        if (trimmedName.length() < 2) {
            return false;
        }

        // Name can only contain letters, spaces, hyphens, and apostrophes
        return trimmedName.matches("^[a-zA-ZÀ-ÿ\\s'-]+$");
    }

    /**
     * Validate numeric value is positive
     * @param value Value to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Validate string is not null or empty
     * @param str String to validate
     * @return true if not null/empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate string length
     * @param str String to validate
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if length is valid, false otherwise
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate GPS coordinates format (latitude, longitude)
     * @param coordinates GPS coordinates string
     * @return true if valid, false otherwise
     */
    public static boolean isValidGPSCoordinates(String coordinates) {
        if (coordinates == null || coordinates.trim().isEmpty()) {
            return false;
        }

        // Format: latitude,longitude (e.g., 14.6928,-17.4467)
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            return false;
        }

        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            // Valid latitude: -90 to 90
            // Valid longitude: -180 to 180
            return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate registration number format
     * @param regNumber Registration number
     * @return true if valid, false otherwise
     */
    public static boolean isValidRegistrationNumber(String regNumber) {
        if (regNumber == null || regNumber.trim().isEmpty()) {
            return false;
        }

        // Format: MAT-2024-001 or similar
        return regNumber.matches("^[A-Z]{3}-[0-9]{4}-[0-9]{3,4}$");
    }

    /**
     * Format phone number to standard format
     * @param phone Phone number to format
     * @return Formatted phone number
     */
    public static String formatPhone(String phone) {
        if (phone == null) {
            return "";
        }

        // Remove all non-digits
        String digits = phone.replaceAll("[^0-9]", "");

        // Remove country code if present
        if (digits.startsWith("221")) {
            digits = digits.substring(3);
        }

        // Format as XX XXX XX XX
        if (digits.length() == 9) {
            return String.format("%s %s %s %s",
                    digits.substring(0, 2),
                    digits.substring(2, 5),
                    digits.substring(5, 7),
                    digits.substring(7, 9));
        }

        return phone;
    }

    /**
     * Format CNI number
     * @param cni CNI number to format
     * @return Formatted CNI
     */
    public static String formatCNI(String cni) {
        if (cni == null) {
            return "";
        }

        // Remove all non-digits
        String digits = cni.replaceAll("[^0-9]", "");

        // Format as X XXXX XXXXX XXX X
        if (digits.length() == 13) {
            return String.format("%s %s %s %s %s",
                    digits.substring(0, 1),
                    digits.substring(1, 5),
                    digits.substring(5, 10),
                    digits.substring(10, 13));
        }

        return cni;
    }

    /**
     * Sanitize string input (remove dangerous characters)
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'%;()&+]", "").trim();
    }

    /**
     * Get validation error message
     * @param fieldName Field name
     * @param validationType Type of validation that failed
     * @return Error message
     */
    public static String getValidationError(String fieldName, String validationType) {
        return switch (validationType.toLowerCase()) {
            case "empty" -> fieldName + " cannot be empty";
            case "email" -> "Invalid email address format";
            case "phone" -> "Invalid phone number format (9 digits required)";
            case "cni" -> "Invalid CNI format (13 digits required)";
            case "name" -> fieldName + " can only contain letters";
            case "positive" -> fieldName + " must be positive";
            case "length" -> fieldName + " length is invalid";
            default -> fieldName + " is invalid";
        };
    }
}