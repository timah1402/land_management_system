package dao;

import database.DatabaseConfig;
import models.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Region operations
 */
public class RegionDAO {

    /**
     * Get region by ID
     */
    public Region getRegionById(int regionId) {
        String sql = "SELECT * FROM Regions WHERE region_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, regionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRegionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting region by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get region by code
     */
    public Region getRegionByCode(String regionCode) {
        String sql = "SELECT * FROM Regions WHERE code_region = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regionCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRegionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting region by code: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get region by name
     */
    public Region getRegionByName(String regionName) {
        String sql = "SELECT * FROM Regions WHERE nom_region = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regionName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRegionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting region by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all regions
     */
    public List<Region> getAllRegions() {
        List<Region> regions = new ArrayList<>();
        String sql = "SELECT * FROM Regions ORDER BY nom_region";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                regions.add(extractRegionFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all regions: " + e.getMessage());
            e.printStackTrace();
        }
        return regions;
    }

    /**
     * Get all region names (useful for dropdowns)
     */
    public List<String> getAllRegionNames() {
        List<String> regionNames = new ArrayList<>();
        String sql = "SELECT nom_region FROM Regions ORDER BY nom_region";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                regionNames.add(rs.getString("nom_region"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting region names: " + e.getMessage());
            e.printStackTrace();
        }
        return regionNames;
    }

    /**
     * Get region count
     */
    public int getRegionCount() {
        String sql = "SELECT COUNT(*) FROM Regions";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting region count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Region object from ResultSet
     */
    private Region extractRegionFromResultSet(ResultSet rs) throws SQLException {
        Region region = new Region();
        region.setRegionId(rs.getInt("region_id"));
        region.setRegionCode(rs.getString("code_region"));
        region.setRegionName(rs.getString("nom_region"));
        region.setCapital(rs.getString("chef_lieu"));
        region.setArea(rs.getDouble("superficie"));
        region.setPopulation(rs.getInt("population"));
        region.setCreatedAt(rs.getTimestamp("date_creation"));
        return region;
    }
}