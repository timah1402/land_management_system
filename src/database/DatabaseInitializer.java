package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe pour initialiser la base de données et créer toutes les tables
 */
public class DatabaseInitializer {

    /**
     * Initialiser toutes les tables de la base de données
     * @return true si l'initialisation réussit, false sinon
     */
    public static boolean initializeDatabase() {
        Connection conn = DatabaseConfig.getConnection();

        if (conn == null) {
            System.err.println("✗ Impossible d'initialiser la base de données: connexion nulle");
            return false;
        }

        try {
            Statement stmt = conn.createStatement();

            System.out.println("Création des tables...");

            // 1. Table Regions
            stmt.execute(createRegionsTable());
            System.out.println("✓ Table Regions créée");

            // 2. Table Users
            stmt.execute(createUsersTable());
            System.out.println("✓ Table Users créée");

            // 3. Table Administrateurs
            stmt.execute(createAdministrateursTable());
            System.out.println("✓ Table Administrateurs créée");

            // 4. Table AgentsFonciers
            stmt.execute(createAgentsFonciersTable());
            System.out.println("✓ Table AgentsFonciers créée");

            // 5. Table Citoyens
            stmt.execute(createCitoyensTable());
            System.out.println("✓ Table Citoyens créée");

            // 6. Table Parcelles
            stmt.execute(createParcellesTable());
            System.out.println("✓ Table Parcelles créée");

            // 7. Table Documents
            stmt.execute(createDocumentsTable());
            System.out.println("✓ Table Documents créée");

            // 8. Table Transactions
            stmt.execute(createTransactionsTable());
            System.out.println("✓ Table Transactions créée");

            // 9. Table Litiges
            stmt.execute(createLitigesTable());
            System.out.println("✓ Table Litiges créée");

            // 10. Table Notifications
            stmt.execute(createNotificationsTable());
            System.out.println("✓ Table Notifications créée");

            // 11. Table AuditLog
            stmt.execute(createAuditLogTable());
            System.out.println("✓ Table AuditLog créée");

            // Insertion des données de référence (Régions du Sénégal)
            insertRegionsData(stmt);
            System.out.println("✓ Données des régions insérées");

            stmt.close();

            System.out.println("\n✓✓✓ Base de données initialisée avec succès! ✓✓✓\n");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================================
    // Méthodes privées pour créer chaque table
    // ========================================================================

    private static String createRegionsTable() {
        return """
            CREATE TABLE IF NOT EXISTS Regions (
                region_id INTEGER PRIMARY KEY AUTOINCREMENT,
                code_region TEXT NOT NULL UNIQUE,
                nom_region TEXT NOT NULL UNIQUE,
                chef_lieu TEXT NOT NULL,
                superficie REAL,
                population INTEGER,
                date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
    }

    private static String createUsersTable() {
        return """
            CREATE TABLE IF NOT EXISTS Users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                telephone TEXT NOT NULL,
                mot_de_passe TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('ADMIN', 'AGENT', 'CITIZEN')),
                account_status TEXT DEFAULT 'PENDING' CHECK(account_status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'REJECTED')),
                date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                derniere_connexion TIMESTAMP
            )
            """;
    }

    private static String createAdministrateursTable() {
        return """
            CREATE TABLE IF NOT EXISTS Administrateurs (
                admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL UNIQUE,
                niveau_acces TEXT DEFAULT 'FULL' CHECK(niveau_acces IN ('FULL', 'LIMITED')),
                departement TEXT,
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
            )
            """;
    }

    private static String createAgentsFonciersTable() {
        return """
            CREATE TABLE IF NOT EXISTS AgentsFonciers (
                agent_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL UNIQUE,
                matricule TEXT NOT NULL UNIQUE,
                region TEXT NOT NULL,
                specialisation TEXT,
                date_nomination DATE,
                statut TEXT DEFAULT 'ACTIVE' CHECK(statut IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
                FOREIGN KEY (region) REFERENCES Regions(nom_region)
            )
            """;
    }

    private static String createCitoyensTable() {
        return """
            CREATE TABLE IF NOT EXISTS Citoyens (
                citoyen_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL UNIQUE,
                numero_cni TEXT UNIQUE,
                date_naissance DATE,
                lieu_naissance TEXT,
                adresse_complete TEXT,
                profession TEXT,
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
            )
            """;
    }

    private static String createParcellesTable() {
        return """
            CREATE TABLE IF NOT EXISTS Parcelles (
                parcelle_id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_parcelle TEXT NOT NULL UNIQUE,
                titre_foncier TEXT UNIQUE,
                superficie REAL NOT NULL CHECK(superficie > 0),
                unite_superficie TEXT DEFAULT 'HECTARE' CHECK(unite_superficie IN ('M2', 'HECTARE')),
                type_terrain TEXT NOT NULL CHECK(type_terrain IN ('RESIDENTIAL', 'COMMERCIAL', 'AGRICULTURAL', 'INDUSTRIAL', 'MIXED')),
                usage_actuel TEXT,
                adresse TEXT NOT NULL,
                region TEXT NOT NULL,
                departement TEXT,
                commune TEXT,
                coordonnees_gps TEXT,
                statut_parcelle TEXT DEFAULT 'AVAILABLE' CHECK(statut_parcelle IN ('AVAILABLE', 'OCCUPIED', 'IN_TRANSACTION', 'IN_DISPUTE', 'RESERVED')),
                valeur_estimee REAL,
                proprietaire_actuel INTEGER,
                date_acquisition DATE,
                date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                derniere_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                notes TEXT,
                FOREIGN KEY (proprietaire_actuel) REFERENCES Citoyens(citoyen_id) ON DELETE SET NULL,
                FOREIGN KEY (region) REFERENCES Regions(nom_region)
            )
            """;
    }

    private static String createDocumentsTable() {
        return """
            CREATE TABLE IF NOT EXISTS Documents (
                document_id INTEGER PRIMARY KEY AUTOINCREMENT,
                type_document TEXT NOT NULL CHECK(type_document IN ('LAND_TITLE', 'SALE_DEED', 'CONTRACT', 'CERTIFICATE', 'PLAN', 'PHOTO', 'OTHER')),
                nom_fichier TEXT NOT NULL,
                chemin_fichier TEXT NOT NULL,
                taille_fichier INTEGER,
                format_fichier TEXT,
                parcelle_id INTEGER,
                transaction_id INTEGER,
                user_id INTEGER,
                date_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                description TEXT,
                FOREIGN KEY (parcelle_id) REFERENCES Parcelles(parcelle_id) ON DELETE CASCADE,
                FOREIGN KEY (transaction_id) REFERENCES Transactions(transaction_id) ON DELETE CASCADE,
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
            )
            """;
    }

    private static String createTransactionsTable() {
        return """
            CREATE TABLE IF NOT EXISTS Transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                parcelle_id INTEGER NOT NULL,
                type_transaction TEXT NOT NULL CHECK(type_transaction IN ('SALE', 'PURCHASE', 'TRANSFER', 'INHERITANCE', 'DONATION', 'EXCHANGE')),
                ancien_proprietaire INTEGER,
                nouveau_proprietaire INTEGER NOT NULL,
                montant REAL,
                devise TEXT DEFAULT 'XOF',
                date_transaction DATE NOT NULL,
                statut_transaction TEXT DEFAULT 'PENDING' CHECK(statut_transaction IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
                agent_validateur INTEGER,
                date_validation TIMESTAMP,
                frais_transaction REAL,
                taxe_applicable REAL,
                numero_acte TEXT UNIQUE,
                notes TEXT,
                date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (parcelle_id) REFERENCES Parcelles(parcelle_id) ON DELETE CASCADE,
                FOREIGN KEY (ancien_proprietaire) REFERENCES Citoyens(citoyen_id),
                FOREIGN KEY (nouveau_proprietaire) REFERENCES Citoyens(citoyen_id),
                FOREIGN KEY (agent_validateur) REFERENCES AgentsFonciers(agent_id)
            )
            """;
    }

    private static String createLitigesTable() {
        return """
            CREATE TABLE IF NOT EXISTS Litiges (
                litige_id INTEGER PRIMARY KEY AUTOINCREMENT,
                parcelle_id INTEGER NOT NULL,
                plaignant_id INTEGER NOT NULL,
                defendeur_id INTEGER,
                type_litige TEXT NOT NULL CHECK(type_litige IN ('OWNERSHIP', 'BOUNDARY', 'USAGE', 'INHERITANCE', 'OTHER')),
                description TEXT NOT NULL,
                statut_litige TEXT DEFAULT 'OPEN' CHECK(statut_litige IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
                priorite TEXT DEFAULT 'MEDIUM' CHECK(priorite IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
                agent_assigne INTEGER,
                date_ouverture DATE NOT NULL,
                date_resolution DATE,
                resolution TEXT,
                preuves_fournies TEXT,
                FOREIGN KEY (parcelle_id) REFERENCES Parcelles(parcelle_id) ON DELETE CASCADE,
                FOREIGN KEY (plaignant_id) REFERENCES Citoyens(citoyen_id),
                FOREIGN KEY (defendeur_id) REFERENCES Citoyens(citoyen_id),
                FOREIGN KEY (agent_assigne) REFERENCES AgentsFonciers(agent_id)
            )
            """;
    }

    private static String createNotificationsTable() {
        return """
            CREATE TABLE IF NOT EXISTS Notifications (
                notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                type_notification TEXT NOT NULL CHECK(type_notification IN ('TRANSACTION', 'DISPUTE', 'APPROVAL', 'REJECTION', 'SYSTEM')),
                titre TEXT NOT NULL,
                message TEXT NOT NULL,
                lue INTEGER DEFAULT 0 CHECK(lue IN (0, 1)),
                date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                date_lecture TIMESTAMP,
                lien_reference TEXT,
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
            )
            """;
    }

    private static String createAuditLogTable() {
        return """
            CREATE TABLE IF NOT EXISTS AuditLog (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action TEXT NOT NULL,
                table_affectee TEXT,
                enregistrement_id INTEGER,
                anciennes_valeurs TEXT,
                nouvelles_valeurs TEXT,
                adresse_ip TEXT,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
            )
            """;
    }

    // ========================================================================
    // Insertion des données de référence (Régions du Sénégal)
    // ========================================================================

    private static void insertRegionsData(Statement stmt) throws SQLException {
        String[] regions = {
                "('DK', 'Dakar', 'Dakar', 547, 3140000)",
                "('TH', 'Thiès', 'Thiès', 6670, 1788000)",
                "('SL', 'Saint-Louis', 'Saint-Louis', 19034, 1028000)",
                "('DI', 'Diourbel', 'Diourbel', 4359, 1498000)",
                "('LG', 'Louga', 'Louga', 29188, 977000)",
                "('MT', 'Matam', 'Matam', 29445, 626000)",
                "('TB', 'Tambacounda', 'Tambacounda', 59602, 711000)",
                "('KL', 'Kaolack', 'Kaolack', 16010, 1027000)",
                "('FT', 'Fatick', 'Fatick', 7935, 815000)",
                "('KF', 'Kaffrine', 'Kaffrine', 11262, 600000)",
                "('KD', 'Kolda', 'Kolda', 21011, 745000)",
                "('ZG', 'Ziguinchor', 'Ziguinchor', 7339, 594000)",
                "('SE', 'Sédhiou', 'Sédhiou', 7293, 525000)",
                "('KE', 'Kédougou', 'Kédougou', 16896, 185000)"
        };

        for (String region : regions) {
            stmt.execute("INSERT OR IGNORE INTO Regions (code_region, nom_region, chef_lieu, superficie, population) VALUES " + region);
        }
    }

    /**
     * Supprimer toutes les tables (utilisé pour réinitialiser)
     */
    public static void dropAllTables() {
        Connection conn = DatabaseConfig.getConnection();

        try {
            Statement stmt = conn.createStatement();

            // Désactiver temporairement les clés étrangères
            stmt.execute("PRAGMA foreign_keys = OFF;");

            // Supprimer toutes les tables dans l'ordre inverse
            stmt.execute("DROP TABLE IF EXISTS AuditLog");
            stmt.execute("DROP TABLE IF EXISTS Notifications");
            stmt.execute("DROP TABLE IF EXISTS Litiges");
            stmt.execute("DROP TABLE IF EXISTS Documents");
            stmt.execute("DROP TABLE IF EXISTS Transactions");
            stmt.execute("DROP TABLE IF EXISTS Parcelles");
            stmt.execute("DROP TABLE IF EXISTS Citoyens");
            stmt.execute("DROP TABLE IF EXISTS AgentsFonciers");
            stmt.execute("DROP TABLE IF EXISTS Administrateurs");
            stmt.execute("DROP TABLE IF EXISTS Users");
            stmt.execute("DROP TABLE IF EXISTS Regions");

            // Réactiver les clés étrangères
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.close();
            System.out.println("✓ Toutes les tables ont été supprimées");

        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la suppression des tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}