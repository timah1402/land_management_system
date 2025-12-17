package models;

import java.sql.Date;

/**
 * LandAgent class representing land agents/officers
 */
public class LandAgent extends User {

    // Attributes
    private int agentId;
    private String registrationNumber;
    private String region;
    private String specialization;
    private Date appointmentDate;
    private AgentStatus status;

    // Enum
    public enum AgentStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    // Constructors
    public LandAgent() {
        super();
    }

    public LandAgent(String lastName, String firstName, String email, String phone,
                     String password, String registrationNumber, String region,
                     String specialization) {
        super(lastName, firstName, email, phone, password, UserRole.AGENT);
        this.registrationNumber = registrationNumber;
        this.region = region;
        this.specialization = specialization;
        this.status = AgentStatus.ACTIVE;
    }

    // Getters and Setters
    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LandAgent{" +
                "agentId=" + agentId +
                ", userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", region='" + region + '\'' +
                ", specialization='" + specialization + '\'' +
                ", status=" + status +
                '}';
    }
}