package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe de configuration et gestion de la connexion à la base de données SQLite
 */
public class DatabaseConfig {

    // Chemin de la base de données SQLite
    private static final String DB_URL = "jdbc:sqlite:land_management.db";

    // Instance unique de connexion (Singleton pattern)
    private static Connection connection = null;

    /**
     * Obtenir une connexion à la base de données
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Charger le driver SQLite
                Class.forName("org.sqlite.JDBC");

                // Établir la connexion
                connection = DriverManager.getConnection(DB_URL);

                // Activer les clés étrangères (très important pour SQLite!)
                Statement stmt = connection.createStatement();
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.close();

                System.out.println("✓ Connexion à la base de données établie avec succès");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver SQLite non trouvé: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fermer la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tester la connexion à la base de données
     * @return true si la connexion fonctionne, false sinon
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtenir le chemin de la base de données
     * @return String contenant le chemin
     */
    public static String getDatabasePath() {
        return DB_URL;
    }
}