package models;

import java.sql.Timestamp;

/**
 * Region class representing the 14 regions of Senegal
 */
public class Region {

    // Attributes
    private int regionId;
    private String regionCode;
    private String regionName;
    private String capital;
    private double area;
    private int population;
    private Timestamp createdAt;

    // Constructors
    public Region() {
    }

    public Region(String regionCode, String regionName, String capital,
                  double area, int population) {
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.capital = capital;
        this.area = area;
        this.population = population;
    }

    // Getters and Setters
    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Region{" +
                "regionId=" + regionId +
                ", code='" + regionCode + '\'' +
                ", name='" + regionName + '\'' +
                ", capital='" + capital + '\'' +
                ", area=" + area + " km²" +
                ", population=" + population +
                '}';
    }
}