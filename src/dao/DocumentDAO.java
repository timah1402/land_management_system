package dao;

import database.DatabaseConfig;
import models.Document;
import models.Document.DocumentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Document operations
 */
public class DocumentDAO {

    /**
     * Create a new document
     */
    public boolean createDocument(Document document) {
        String sql = "INSERT INTO Documents (type_document, nom_fichier, chemin_fichier, taille_fichier, " +
                "format_fichier, parcelle_id, transaction_id, user_id, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, document.getType().name());
            pstmt.setString(2, document.getFileName());
            pstmt.setString(3, document.getFilePath());
            pstmt.setLong(4, document.getFileSize());
            pstmt.setString(5, document.getFileFormat());

            if (document.getParcelId() != null) {
                pstmt.setInt(6, document.getParcelId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (document.getTransactionId() != null) {
                pstmt.setInt(7, document.getTransactionId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            if (document.getUserId() != null) {
                pstmt.setInt(8, document.getUserId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            pstmt.setString(9, document.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    document.setDocumentId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get document by ID
     */
    public Document getDocumentById(int documentId) {
        String sql = "SELECT * FROM Documents WHERE document_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDocumentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting document by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update document
     */
    public boolean updateDocument(Document document) {
        String sql = "UPDATE Documents SET type_document = ?, nom_fichier = ?, chemin_fichier = ?, " +
                "taille_fichier = ?, format_fichier = ?, description = ? WHERE document_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getType().name());
            pstmt.setString(2, document.getFileName());
            pstmt.setString(3, document.getFilePath());
            pstmt.setLong(4, document.getFileSize());
            pstmt.setString(5, document.getFileFormat());
            pstmt.setString(6, document.getDescription());
            pstmt.setInt(7, document.getDocumentId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete document
     */
    public boolean deleteDocument(int documentId) {
        String sql = "DELETE FROM Documents WHERE document_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting document: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all documents
     */
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all documents: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Get documents by parcel
     */
    public List<Document> getDocumentsByParcel(int parcelId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE parcelle_id = ? ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parcelId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting documents by parcel: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Get documents by transaction
     */
    public List<Document> getDocumentsByTransaction(int transactionId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE transaction_id = ? ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting documents by transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Get documents by user
     */
    public List<Document> getDocumentsByUser(int userId) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE user_id = ? ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting documents by user: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Get documents by type
     */
    public List<Document> getDocumentsByType(DocumentType type) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE type_document = ? ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting documents by type: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Search documents by filename
     */
    public List<Document> searchDocumentsByFilename(String searchTerm) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE nom_fichier LIKE ? ORDER BY date_upload DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching documents: " + e.getMessage());
            e.printStackTrace();
        }
        return documents;
    }

    /**
     * Get document count
     */
    public int getDocumentCount() {
        String sql = "SELECT COUNT(*) FROM Documents";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting document count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Document object from ResultSet
     */
    private Document extractDocumentFromResultSet(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setDocumentId(rs.getInt("document_id"));
        document.setType(DocumentType.valueOf(rs.getString("type_document")));
        document.setFileName(rs.getString("nom_fichier"));
        document.setFilePath(rs.getString("chemin_fichier"));
        document.setFileSize(rs.getLong("taille_fichier"));
        document.setFileFormat(rs.getString("format_fichier"));

        int parcelId = rs.getInt("parcelle_id");
        if (!rs.wasNull()) {
            document.setParcelId(parcelId);
        }

        int transactionId = rs.getInt("transaction_id");
        if (!rs.wasNull()) {
            document.setTransactionId(transactionId);
        }

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            document.setUserId(userId);
        }

        document.setUploadedAt(rs.getTimestamp("date_upload"));
        document.setDescription(rs.getString("description"));

        return document;
    }
}