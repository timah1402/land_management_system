package dao;

import database.DatabaseConfig;
import models.Parcel;
import models.Parcel.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Parcel operations
 */
public class ParcelDAO {

    /**
     * Create a new parcel
     */
    public boolean createParcel(Parcel parcel) {
        String sql = "INSERT INTO Parcelles (numero_parcelle, titre_foncier, superficie, unite_superficie, " +
                "type_terrain, usage_actuel, adresse, region, departement, commune, coordonnees_gps, " +
                "statut_parcelle, valeur_estimee, proprietaire_actuel, date_acquisition, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, parcel.getParcelNumber());
            pstmt.setString(2, parcel.getLandTitle());
            pstmt.setDouble(3, parcel.getArea());
            pstmt.setString(4, parcel.getAreaUnit().name());
            pstmt.setString(5, parcel.getLandType().name());
            pstmt.setString(6, parcel.getCurrentUsage());
            pstmt.setString(7, parcel.getAddress());
            pstmt.setString(8, parcel.getRegion());
            pstmt.setString(9, parcel.getDepartment());
            pstmt.setString(10, parcel.getCommune());
            pstmt.setString(11, parcel.getGpsCoordinates());
            pstmt.setString(12, parcel.getStatus().name());

            if (parcel.getEstimatedValue() != null) {
                pstmt.setBigDecimal(13, parcel.getEstimatedValue());
            } else {
                pstmt.setNull(13, Types.DECIMAL);
            }

            if (parcel.getCurrentOwnerId() > 0) {
                pstmt.setInt(14, parcel.getCurrentOwnerId());
            } else {
                pstmt.setNull(14, Types.INTEGER);
            }

            pstmt.setDate(15, parcel.getAcquisitionDate());
            pstmt.setString(16, parcel.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    parcel.setParcelId(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error creating parcel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get parcel by ID
     */
    public Parcel getParcelById(int parcelId) {
        String sql = "SELECT * FROM Parcelles WHERE parcelle_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parcelId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractParcelFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcel by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get parcel by parcel number
     */
    public Parcel getParcelByNumber(String parcelNumber) {
        String sql = "SELECT * FROM Parcelles WHERE numero_parcelle = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parcelNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractParcelFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcel by number: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get parcel by land title
     */
    public Parcel getParcelByLandTitle(String landTitle) {
        String sql = "SELECT * FROM Parcelles WHERE titre_foncier = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, landTitle);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractParcelFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcel by land title: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update parcel
     */
    public boolean updateParcel(Parcel parcel) {
        String sql = "UPDATE Parcelles SET numero_parcelle = ?, titre_foncier = ?, superficie = ?, " +
                "unite_superficie = ?, type_terrain = ?, usage_actuel = ?, adresse = ?, " +
                "region = ?, departement = ?, commune = ?, coordonnees_gps = ?, " +
                "statut_parcelle = ?, valeur_estimee = ?, proprietaire_actuel = ?, " +
                "date_acquisition = ?, notes = ?, derniere_modification = CURRENT_TIMESTAMP " +
                "WHERE parcelle_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parcel.getParcelNumber());
            pstmt.setString(2, parcel.getLandTitle());
            pstmt.setDouble(3, parcel.getArea());
            pstmt.setString(4, parcel.getAreaUnit().name());
            pstmt.setString(5, parcel.getLandType().name());
            pstmt.setString(6, parcel.getCurrentUsage());
            pstmt.setString(7, parcel.getAddress());
            pstmt.setString(8, parcel.getRegion());
            pstmt.setString(9, parcel.getDepartment());
            pstmt.setString(10, parcel.getCommune());
            pstmt.setString(11, parcel.getGpsCoordinates());
            pstmt.setString(12, parcel.getStatus().name());

            if (parcel.getEstimatedValue() != null) {
                pstmt.setBigDecimal(13, parcel.getEstimatedValue());
            } else {
                pstmt.setNull(13, Types.DECIMAL);
            }

            if (parcel.getCurrentOwnerId() > 0) {
                pstmt.setInt(14, parcel.getCurrentOwnerId());
            } else {
                pstmt.setNull(14, Types.INTEGER);
            }

            pstmt.setDate(15, parcel.getAcquisitionDate());
            pstmt.setString(16, parcel.getNotes());
            pstmt.setInt(17, parcel.getParcelId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parcel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update parcel status
     */
    public boolean updateParcelStatus(int parcelId, ParcelStatus status) {
        String sql = "UPDATE Parcelles SET statut_parcelle = ?, derniere_modification = CURRENT_TIMESTAMP " +
                "WHERE parcelle_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, parcelId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parcel status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update parcel owner
     */
    public boolean updateParcelOwner(int parcelId, int newOwnerId) {
        String sql = "UPDATE Parcelles SET proprietaire_actuel = ?, date_acquisition = CURRENT_DATE, " +
                "derniere_modification = CURRENT_TIMESTAMP WHERE parcelle_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newOwnerId);
            pstmt.setInt(2, parcelId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parcel owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete parcel
     */
    public boolean deleteParcel(int parcelId) {
        String sql = "DELETE FROM Parcelles WHERE parcelle_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parcelId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting parcel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all parcels
     */
    public List<Parcel> getAllParcels() {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all parcels: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Get parcels by owner
     */
    public List<Parcel> getParcelsByOwner(int ownerId) {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles WHERE proprietaire_actuel = ? ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcels by owner: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Get parcels by region
     */
    public List<Parcel> getParcelsByRegion(String region) {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles WHERE region = ? ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, region);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcels by region: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Get parcels by status
     */
    public List<Parcel> getParcelsByStatus(ParcelStatus status) {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles WHERE statut_parcelle = ? ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcels by status: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Get parcels by land type
     */
    public List<Parcel> getParcelsByLandType(LandType landType) {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles WHERE type_terrain = ? ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, landType.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcels by land type: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Search parcels by address or parcel number
     */
    public List<Parcel> searchParcels(String searchTerm) {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT * FROM Parcelles WHERE adresse LIKE ? OR numero_parcelle LIKE ? " +
                "OR titre_foncier LIKE ? ORDER BY date_enregistrement DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                parcels.add(extractParcelFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching parcels: " + e.getMessage());
            e.printStackTrace();
        }
        return parcels;
    }

    /**
     * Check if parcel number exists
     */
    public boolean parcelNumberExists(String parcelNumber) {
        String sql = "SELECT COUNT(*) FROM Parcelles WHERE numero_parcelle = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parcelNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking parcel number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get total parcel count
     */
    public int getParcelCount() {
        String sql = "SELECT COUNT(*) FROM Parcelles";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting parcel count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Parcel object from ResultSet
     */
    private Parcel extractParcelFromResultSet(ResultSet rs) throws SQLException {
        Parcel parcel = new Parcel();
        parcel.setParcelId(rs.getInt("parcelle_id"));
        parcel.setParcelNumber(rs.getString("numero_parcelle"));
        parcel.setLandTitle(rs.getString("titre_foncier"));
        parcel.setArea(rs.getDouble("superficie"));
        parcel.setAreaUnit(AreaUnit.valueOf(rs.getString("unite_superficie")));
        parcel.setLandType(LandType.valueOf(rs.getString("type_terrain")));
        parcel.setCurrentUsage(rs.getString("usage_actuel"));
        parcel.setAddress(rs.getString("adresse"));
        parcel.setRegion(rs.getString("region"));
        parcel.setDepartment(rs.getString("departement"));
        parcel.setCommune(rs.getString("commune"));
        parcel.setGpsCoordinates(rs.getString("coordonnees_gps"));
        parcel.setStatus(ParcelStatus.valueOf(rs.getString("statut_parcelle")));
        parcel.setEstimatedValue(rs.getBigDecimal("valeur_estimee"));
        parcel.setCurrentOwnerId(rs.getInt("proprietaire_actuel"));
        parcel.setAcquisitionDate(rs.getDate("date_acquisition"));
        parcel.setRegisteredAt(rs.getTimestamp("date_enregistrement"));
        parcel.setLastModified(rs.getTimestamp("derniere_modification"));
        parcel.setNotes(rs.getString("notes"));
        return parcel;
    }
}