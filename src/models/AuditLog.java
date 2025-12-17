package models;

import java.sql.Timestamp;

/**
 * AuditLog class representing audit trail entries for system actions
 */
public class AuditLog {

    // Attributes
    private int logId;
    private Integer userId;
    private String action;
    private String affectedTable;
    private Integer recordId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private Timestamp timestamp;

    // Constructors
    public AuditLog() {
    }

    public AuditLog(Integer userId, String action, String affectedTable,
                    Integer recordId, String ipAddress) {
        this.userId = userId;
        this.action = action;
        this.affectedTable = affectedTable;
        this.recordId = recordId;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAffectedTable() {
        return affectedTable;
    }

    public void setAffectedTable(String affectedTable) {
        this.affectedTable = affectedTable;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", affectedTable='" + affectedTable + '\'' +
                ", recordId=" + recordId +
                ", timestamp=" + timestamp +
                '}';
    }
}