package main;

import database.DatabaseConfig;
import database.DatabaseInitializer;

/**
 * Classe principale pour tester la configuration de la base de données
 */
public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  TEST DE CONFIGURATION DE LA BASE DE DONNÉES");
        System.out.println("==============================================\n");

        // Test 1: Connexion à la base de données
        System.out.println("Test 1: Connexion à la base de données");
        System.out.println("----------------------------------------------");
        boolean connectionOk = DatabaseConfig.testConnection();
        if (connectionOk) {
            System.out.println("✓ Test de connexion réussi\n");
        } else {
            System.err.println("✗ Test de connexion échoué\n");
            return;
        }

        // Test 2: Initialisation de la base de données
        System.out.println("Test 2: Initialisation de la base de données");
        System.out.println("----------------------------------------------");
        boolean initOk = DatabaseInitializer.initializeDatabase();
        if (initOk) {
            System.out.println("✓ Initialisation réussie\n");
        } else {
            System.err.println("✗ Initialisation échouée\n");
            return;
        }

        // Afficher le chemin de la base de données
        System.out.println("==============================================");
        System.out.println("Base de données créée avec succès!");
        System.out.println("Chemin: " + DatabaseConfig.getDatabasePath());
        System.out.println("==============================================\n");

        // Fermer la connexion
        DatabaseConfig.closeConnection();
    }
}