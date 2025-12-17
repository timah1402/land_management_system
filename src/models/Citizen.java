package models;

import java.sql.Date;

/**
 * Citizen class representing citizens/property owners
 */
public class Citizen extends User {

    // Attributes
    private int citizenId;
    private String idCardNumber;
    private Date dateOfBirth;
    private String placeOfBirth;
    private String fullAddress;
    private String occupation;

    // Constructors
    public Citizen() {
        super();
    }

    public Citizen(String lastName, String firstName, String email, String phone,
                   String password, String idCardNumber, Date dateOfBirth,
                   String placeOfBirth, String fullAddress, String occupation) {
        super(lastName, firstName, email, phone, password, UserRole.CITIZEN);
        this.idCardNumber = idCardNumber;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.fullAddress = fullAddress;
        this.occupation = occupation;
    }

    // Getters and Setters
    public int getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(int citizenId) {
        this.citizenId = citizenId;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public String toString() {
        return "Citizen{" +
                "citizenId=" + citizenId +
                ", userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}