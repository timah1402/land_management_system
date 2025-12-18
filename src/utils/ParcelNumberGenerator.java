package utils;

import dao.ParcelDAO;
import java.time.LocalDate;

/**
 * Utility class to generate unique parcel numbers in Senegalese format
 * Format: REGION_CODE-YEAR-SEQUENTIAL_NUMBER
 * Example: SL-2024-1547, DK-2025-0001
 */
public class ParcelNumberGenerator {

    private static final ParcelDAO parcelDAO = new ParcelDAO();

    /**
     * Generate a unique parcel number for a given region
     * @param regionName Full region name (e.g., "Saint-Louis", "Dakar")
     * @return Generated parcel number (e.g., "SL-2024-1547")
     */
    public static String generateParcelNumber(String regionName) {
        String regionCode = getRegionCode(regionName);
        int currentYear = LocalDate.now().getYear();
        int sequentialNumber = getNextSequentialNumber(regionCode, currentYear);

        return String.format("%s-%d-%04d", regionCode, currentYear, sequentialNumber);
    }

    /**
     * Get the 2-letter region code from full region name
     */
    private static String getRegionCode(String regionName) {
        if (regionName == null || regionName.isEmpty()) {
            return "XX";
        }

        // Map of Senegalese regions to their codes
        switch (regionName.toUpperCase()) {
            case "DAKAR": return "DK";
            case "THIÈS":
            case "THIES": return "TH";
            case "SAINT-LOUIS":
            case "SAINT LOUIS": return "SL";
            case "DIOURBEL": return "DI";
            case "LOUGA": return "LG";
            case "MATAM": return "MT";
            case "TAMBACOUNDA": return "TB";
            case "KAOLACK": return "KL";
            case "FATICK": return "FT";
            case "KAFFRINE": return "KF";
            case "KOLDA": return "KD";
            case "ZIGUINCHOR": return "ZG";
            case "SÉDHIOU":
            case "SEDHIOU": return "SE";
            case "KÉDOUGOU":
            case "KEDOUGOU": return "KE";
            default:
                // For unknown regions, use first 2 letters
                return regionName.substring(0, Math.min(2, regionName.length())).toUpperCase();
        }
    }

    /**
     * Get the next sequential number for a region and year
     * Searches existing parcels to find the highest number and increments it
     */
    private static int getNextSequentialNumber(String regionCode, int year) {
        String searchPattern = regionCode + "-" + year + "-";
        int highestNumber = 0;

        try {
            // Search all parcels to find those matching the pattern
            var allParcels = parcelDAO.getAllParcels();

            for (var parcel : allParcels) {
                String parcelNumber = parcel.getParcelNumber();
                if (parcelNumber != null && parcelNumber.startsWith(searchPattern)) {
                    try {
                        // Extract the sequential number part
                        String numberPart = parcelNumber.substring(searchPattern.length());
                        int number = Integer.parseInt(numberPart);
                        if (number > highestNumber) {
                            highestNumber = number;
                        }
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Skip invalid format
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding next sequential number: " + e.getMessage());
        }

        // Return next number (highest + 1)
        return highestNumber + 1;
    }

    /**
     * Validate a parcel number format
     * @param parcelNumber The parcel number to validate
     * @return true if format is valid (XX-YYYY-NNNN)
     */
    public static boolean isValidFormat(String parcelNumber) {
        if (parcelNumber == null || parcelNumber.isEmpty()) {
            return false;
        }

        // Format: XX-YYYY-NNNN (2 letters, 4 digits year, 4 digits number)
        String regex = "^[A-Z]{2}-\\d{4}-\\d{4}$";
        return parcelNumber.matches(regex);
    }

    /**
     * Extract region code from parcel number
     * @param parcelNumber Full parcel number (e.g., "SL-2024-1547")
     * @return Region code (e.g., "SL") or null if invalid
     */
    public static String extractRegionCode(String parcelNumber) {
        if (parcelNumber != null && parcelNumber.length() >= 2) {
            return parcelNumber.substring(0, 2);
        }
        return null;
    }

    /**
     * Extract year from parcel number
     * @param parcelNumber Full parcel number (e.g., "SL-2024-1547")
     * @return Year or -1 if invalid
     */
    public static int extractYear(String parcelNumber) {
        try {
            if (parcelNumber != null && parcelNumber.length() >= 7) {
                String yearPart = parcelNumber.substring(3, 7);
                return Integer.parseInt(yearPart);
            }
        } catch (Exception e) {
            // Invalid format
        }
        return -1;
    }

    /**
     * Extract sequential number from parcel number
     * @param parcelNumber Full parcel number (e.g., "SL-2024-1547")
     * @return Sequential number or -1 if invalid
     */
    public static int extractSequentialNumber(String parcelNumber) {
        try {
            if (parcelNumber != null && parcelNumber.length() >= 12) {
                String numberPart = parcelNumber.substring(8);
                return Integer.parseInt(numberPart);
            }
        } catch (Exception e) {
            // Invalid format
        }
        return -1;
    }

    /**
     * Get full region name from code
     */
    public static String getRegionNameFromCode(String code) {
        if (code == null || code.length() != 2) {
            return "Unknown";
        }

        switch (code.toUpperCase()) {
            case "DK": return "Dakar";
            case "TH": return "Thiès";
            case "SL": return "Saint-Louis";
            case "DI": return "Diourbel";
            case "LG": return "Louga";
            case "MT": return "Matam";
            case "TB": return "Tambacounda";
            case "KL": return "Kaolack";
            case "FT": return "Fatick";
            case "KF": return "Kaffrine";
            case "KD": return "Kolda";
            case "ZG": return "Ziguinchor";
            case "SE": return "Sédhiou";
            case "KE": return "Kédougou";
            default: return "Unknown";
        }
    }
}