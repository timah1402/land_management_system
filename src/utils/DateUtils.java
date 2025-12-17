package utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date operations
 */
public class DateUtils {

    // Date formats
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Get current date as SQL Date
     * @return Current date
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Get current timestamp
     * @return Current timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Convert String to SQL Date (dd/MM/yyyy format)
     * @param dateStr Date string
     * @return SQL Date or null if parsing fails
     */
    public static Date stringToDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            java.util.Date utilDate = DATE_FORMAT.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert SQL Date to String (dd/MM/yyyy format)
     * @param date SQL Date
     * @return Formatted date string
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * Convert Timestamp to String (dd/MM/yyyy HH:mm:ss format)
     * @param timestamp Timestamp
     * @return Formatted timestamp string
     */
    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return DATETIME_FORMAT.format(timestamp);
    }

    /**
     * Convert Timestamp to short format (dd/MM/yyyy HH:mm)
     * @param timestamp Timestamp
     * @return Formatted timestamp string
     */
    public static String timestampToShortString(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.format(DISPLAY_DATETIME_FORMAT);
    }

    /**
     * Convert LocalDate to SQL Date
     * @param localDate LocalDate
     * @return SQL Date
     */
    public static Date localDateToSqlDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.valueOf(localDate);
    }

    /**
     * Convert SQL Date to LocalDate
     * @param date SQL Date
     * @return LocalDate
     */
    public static LocalDate sqlDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }

    /**
     * Calculate age from birth date
     * @param birthDate Birth date
     * @return Age in years
     */
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }

        LocalDate birth = birthDate.toLocalDate();
        LocalDate now = LocalDate.now();
        return (int) ChronoUnit.YEARS.between(birth, now);
    }

    /**
     * Calculate days between two dates
     * @param startDate Start date
     * @param endDate End date
     * @return Number of days
     */
    public static long daysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Check if date is in the past
     * @param date Date to check
     * @return true if date is in the past, false otherwise
     */
    public static boolean isPast(Date date) {
        if (date == null) {
            return false;
        }
        return date.before(getCurrentDate());
    }

    /**
     * Check if date is in the future
     * @param date Date to check
     * @return true if date is in the future, false otherwise
     */
    public static boolean isFuture(Date date) {
        if (date == null) {
            return false;
        }
        return date.after(getCurrentDate());
    }

    /**
     * Check if date is today
     * @param date Date to check
     * @return true if date is today, false otherwise
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        return date.toLocalDate().equals(LocalDate.now());
    }

    /**
     * Add days to a date
     * @param date Original date
     * @param days Number of days to add (can be negative)
     * @return New date
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toLocalDate();
        LocalDate newDate = localDate.plusDays(days);
        return Date.valueOf(newDate);
    }

    /**
     * Add months to a date
     * @param date Original date
     * @param months Number of months to add (can be negative)
     * @return New date
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toLocalDate();
        LocalDate newDate = localDate.plusMonths(months);
        return Date.valueOf(newDate);
    }

    /**
     * Add years to a date
     * @param date Original date
     * @param years Number of years to add (can be negative)
     * @return New date
     */
    public static Date addYears(Date date, int years) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toLocalDate();
        LocalDate newDate = localDate.plusYears(years);
        return Date.valueOf(newDate);
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "5 minutes ago")
     * @param timestamp Timestamp to compare
     * @return Relative time string
     */
    public static String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        long diffMillis = System.currentTimeMillis() - timestamp.getTime();
        long diffSeconds = diffMillis / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;

        if (diffSeconds < 60) {
            return "Just now";
        } else if (diffMinutes < 60) {
            return diffMinutes + (diffMinutes == 1 ? " minute ago" : " minutes ago");
        } else if (diffHours < 24) {
            return diffHours + (diffHours == 1 ? " hour ago" : " hours ago");
        } else if (diffDays < 7) {
            return diffDays + (diffDays == 1 ? " day ago" : " days ago");
        } else if (diffDays < 30) {
            long weeks = diffDays / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else if (diffDays < 365) {
            long months = diffDays / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        } else {
            long years = diffDays / 365;
            return years + (years == 1 ? " year ago" : " years ago");
        }
    }

    /**
     * Format date for display (dd/MM/yyyy)
     * @param date Date to format
     * @return Formatted string
     */
    public static String formatForDisplay(Date date) {
        if (date == null) {
            return "N/A";
        }
        return date.toLocalDate().format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Format timestamp for display (dd/MM/yyyy HH:mm)
     * @param timestamp Timestamp to format
     * @return Formatted string
     */
    public static String formatForDisplay(Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        return timestamp.toLocalDateTime().format(DISPLAY_DATETIME_FORMAT);
    }

    /**
     * Get month name in French
     * @param month Month number (1-12)
     * @return Month name
     */
    public static String getMonthName(int month) {
        String[] months = {
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        };

        if (month >= 1 && month <= 12) {
            return months[month - 1];
        }
        return "";
    }

    /**
     * Get current year
     * @return Current year
     */
    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * Check if year is valid (between 1900 and current year + 100)
     * @param year Year to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidYear(int year) {
        int currentYear = getCurrentYear();
        return year >= 1900 && year <= currentYear + 100;
    }
}