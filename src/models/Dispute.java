package models;

import java.sql.Date;

/**
 * Dispute class representing land disputes/conflicts
 */
public class Dispute {

    // Attributes
    private int disputeId;
    private int parcelId;
    private int complainantId;
    private Integer defendantId;
    private DisputeType type;
    private String description;
    private DisputeStatus status;
    private Priority priority;
    private Integer assignedAgentId;
    private Date openedDate;
    private Date resolutionDate;
    private String resolution;
    private String evidenceProvided;

    // Enums
    public enum DisputeType {
        OWNERSHIP, BOUNDARY, USAGE, INHERITANCE, OTHER
    }

    public enum DisputeStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Constructors
    public Dispute() {
    }

    public Dispute(int parcelId, int complainantId, DisputeType type,
                   String description, Date openedDate) {
        this.parcelId = parcelId;
        this.complainantId = complainantId;
        this.type = type;
        this.description = description;
        this.openedDate = openedDate;
        this.status = DisputeStatus.OPEN;
        this.priority = Priority.MEDIUM;
    }

    // Getters and Setters
    public int getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(int disputeId) {
        this.disputeId = disputeId;
    }

    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public int getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(int complainantId) {
        this.complainantId = complainantId;
    }

    public Integer getDefendantId() {
        return defendantId;
    }

    public void setDefendantId(Integer defendantId) {
        this.defendantId = defendantId;
    }

    public DisputeType getType() {
        return type;
    }

    public void setType(DisputeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(Integer assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public Date getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(Date openedDate) {
        this.openedDate = openedDate;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getEvidenceProvided() {
        return evidenceProvided;
    }

    public void setEvidenceProvided(String evidenceProvided) {
        this.evidenceProvided = evidenceProvided;
    }

    @Override
    public String toString() {
        return "Dispute{" +
                "disputeId=" + disputeId +
                ", parcelId=" + parcelId +
                ", type=" + type +
                ", status=" + status +
                ", priority=" + priority +
                ", openedDate=" + openedDate +
                '}';
    }
}