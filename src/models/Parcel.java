package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Parcel class representing land parcels
 */
public class Parcel {

    // Attributes
    private int parcelId;
    private String parcelNumber;
    private String landTitle;
    private double area;
    private AreaUnit areaUnit;
    private LandType landType;
    private String currentUsage;
    private String address;
    private String region;
    private String department;
    private String commune;
    private String gpsCoordinates;
    private ParcelStatus status;
    private BigDecimal estimatedValue;
    private int currentOwnerId;
    private Date acquisitionDate;
    private Timestamp registeredAt;
    private Timestamp lastModified;
    private String notes;

    // Enums
    public enum AreaUnit {
        M2, HECTARE
    }

    public enum LandType {
        RESIDENTIAL, COMMERCIAL, AGRICULTURAL, INDUSTRIAL, MIXED
    }

    public enum ParcelStatus {
        AVAILABLE, OCCUPIED, IN_TRANSACTION, IN_DISPUTE, RESERVED
    }

    // Constructors
    public Parcel() {
    }

    public Parcel(String parcelNumber, double area, AreaUnit areaUnit,
                  LandType landType, String address, String region) {
        this.parcelNumber = parcelNumber;
        this.area = area;
        this.areaUnit = areaUnit;
        this.landType = landType;
        this.address = address;
        this.region = region;
        this.status = ParcelStatus.AVAILABLE;
    }

    // Getters and Setters
    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public String getParcelNumber() {
        return parcelNumber;
    }

    public void setParcelNumber(String parcelNumber) {
        this.parcelNumber = parcelNumber;
    }

    public String getLandTitle() {
        return landTitle;
    }

    public void setLandTitle(String landTitle) {
        this.landTitle = landTitle;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public AreaUnit getAreaUnit() {
        return areaUnit;
    }

    public void setAreaUnit(AreaUnit areaUnit) {
        this.areaUnit = areaUnit;
    }

    public LandType getLandType() {
        return landType;
    }

    public void setLandType(LandType landType) {
        this.landType = landType;
    }

    public String getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(String currentUsage) {
        this.currentUsage = currentUsage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public int getCurrentOwnerId() {
        return currentOwnerId;
    }

    public void setCurrentOwnerId(int currentOwnerId) {
        this.currentOwnerId = currentOwnerId;
    }

    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Date acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "parcelId=" + parcelId +
                ", number='" + parcelNumber + '\'' +
                ", title='" + landTitle + '\'' +
                ", area=" + area + " " + areaUnit +
                ", type=" + landType +
                ", region='" + region + '\'' +
                ", status=" + status +
                '}';
    }
}