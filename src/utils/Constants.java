package utils;

/**
 * Application-wide constants
 */
public class Constants {

    // Application Info
    public static final String APP_NAME = "Land Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "Système de Gestion Foncière - Sénégal";

    // Database
    public static final String DB_NAME = "land_management.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_AGENT = "AGENT";
    public static final String ROLE_CITIZEN = "CITIZEN";

    // Account Status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_REJECTED = "REJECTED";

    // Parcel Status
    public static final String PARCEL_AVAILABLE = "AVAILABLE";
    public static final String PARCEL_OCCUPIED = "OCCUPIED";
    public static final String PARCEL_IN_TRANSACTION = "IN_TRANSACTION";
    public static final String PARCEL_IN_DISPUTE = "IN_DISPUTE";
    public static final String PARCEL_RESERVED = "RESERVED";

    // Transaction Status
    public static final String TRANSACTION_PENDING = "PENDING";
    public static final String TRANSACTION_APPROVED = "APPROVED";
    public static final String TRANSACTION_REJECTED = "REJECTED";
    public static final String TRANSACTION_CANCELLED = "CANCELLED";

    // Dispute Status
    public static final String DISPUTE_OPEN = "OPEN";
    public static final String DISPUTE_IN_PROGRESS = "IN_PROGRESS";
    public static final String DISPUTE_RESOLVED = "RESOLVED";
    public static final String DISPUTE_CLOSED = "CLOSED";

    // Land Types
    public static final String LAND_RESIDENTIAL = "RESIDENTIAL";
    public static final String LAND_COMMERCIAL = "COMMERCIAL";
    public static final String LAND_AGRICULTURAL = "AGRICULTURAL";
    public static final String LAND_INDUSTRIAL = "INDUSTRIAL";
    public static final String LAND_MIXED = "MIXED";

    // Document Types
    public static final String DOC_LAND_TITLE = "LAND_TITLE";
    public static final String DOC_SALE_DEED = "SALE_DEED";
    public static final String DOC_CONTRACT = "CONTRACT";
    public static final String DOC_CERTIFICATE = "CERTIFICATE";
    public static final String DOC_PLAN = "PLAN";
    public static final String DOC_PHOTO = "PHOTO";
    public static final String DOC_OTHER = "OTHER";

    // Regions of Senegal (14 regions)
    public static final String[] REGIONS = {
            "Dakar", "Thiès", "Saint-Louis", "Diourbel", "Louga", "Matam",
            "Tambacounda", "Kaolack", "Fatick", "Kaffrine", "Kolda",
            "Ziguinchor", "Sédhiou", "Kédougou"
    };

    // Currency
    public static final String CURRENCY_XOF = "XOF"; // West African CFA Franc
    public static final String CURRENCY_SYMBOL = "FCFA";

    // Validation Rules
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int PHONE_LENGTH = 9; // Senegal phone number
    public static final int CNI_LENGTH = 13; // Senegal ID card

    // Session
    public static final int SESSION_TIMEOUT_HOURS = 8;
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // File Upload
    public static final String UPLOAD_DIRECTORY = "uploads/";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {
            "pdf", "jpg", "jpeg", "png", "doc", "docx"
    };

    // Date Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    // UI Messages
    public static final String MSG_SUCCESS = "Operation completed successfully";
    public static final String MSG_ERROR = "An error occurred";
    public static final String MSG_LOGIN_SUCCESS = "Login successful";
    public static final String MSG_LOGIN_FAILED = "Invalid email or password";
    public static final String MSG_ACCOUNT_PENDING = "Your account is pending approval";
    public static final String MSG_ACCOUNT_SUSPENDED = "Your account has been suspended";
    public static final String MSG_ACCOUNT_REJECTED = "Your account registration was rejected";
    public static final String MSG_SESSION_EXPIRED = "Your session has expired. Please login again";
    public static final String MSG_UNAUTHORIZED = "You are not authorized to perform this action";

    // UI Colors (for Swing components)
    public static final String COLOR_PRIMARY = "#2C3E50";
    public static final String COLOR_SUCCESS = "#27AE60";
    public static final String COLOR_DANGER = "#E74C3C";
    public static final String COLOR_WARNING = "#F39C12";
    public static final String COLOR_INFO = "#3498DB";
    public static final String COLOR_LIGHT = "#ECF0F1";
    public static final String COLOR_DARK = "#34495E";

    // Notification Types
    public static final String NOTIF_TRANSACTION = "TRANSACTION";
    public static final String NOTIF_DISPUTE = "DISPUTE";
    public static final String NOTIF_APPROVAL = "APPROVAL";
    public static final String NOTIF_REJECTION = "REJECTION";
    public static final String NOTIF_SYSTEM = "SYSTEM";

    // Audit Actions
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_ASSIGN = "ASSIGN";

    // Priority Levels
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";

    // Agent Status
    public static final String AGENT_ACTIVE = "ACTIVE";
    public static final String AGENT_INACTIVE = "INACTIVE";
    public static final String AGENT_SUSPENDED = "SUSPENDED";

    // Access Levels
    public static final String ACCESS_FULL = "FULL";
    public static final String ACCESS_LIMITED = "LIMITED";

    // Area Units
    public static final String UNIT_M2 = "M2";
    public static final String UNIT_HECTARE = "HECTARE";

    // Transaction Types
    public static final String TRANS_SALE = "SALE";
    public static final String TRANS_PURCHASE = "PURCHASE";
    public static final String TRANS_TRANSFER = "TRANSFER";
    public static final String TRANS_INHERITANCE = "INHERITANCE";
    public static final String TRANS_DONATION = "DONATION";
    public static final String TRANS_EXCHANGE = "EXCHANGE";

    // Dispute Types
    public static final String DISPUTE_OWNERSHIP = "OWNERSHIP";
    public static final String DISPUTE_BOUNDARY = "BOUNDARY";
    public static final String DISPUTE_USAGE = "USAGE";
    public static final String DISPUTE_INHERITANCE = "INHERITANCE";
    public static final String DISPUTE_OTHER = "OTHER";

    // Window Titles
    public static final String WINDOW_LOGIN = "Login - " + APP_NAME;
    public static final String WINDOW_REGISTER = "Register - " + APP_NAME;
    public static final String WINDOW_ADMIN_DASHBOARD = "Admin Dashboard - " + APP_NAME;
    public static final String WINDOW_AGENT_DASHBOARD = "Agent Dashboard - " + APP_NAME;
    public static final String WINDOW_CITIZEN_DASHBOARD = "Citizen Dashboard - " + APP_NAME;

    // Help Messages
    public static final String HELP_EMAIL = "Enter a valid email address";
    public static final String HELP_PASSWORD = "Password must be at least 8 characters";
    public static final String HELP_PHONE = "Enter 9-digit phone number";
    public static final String HELP_CNI = "Enter 13-digit CNI number";

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}