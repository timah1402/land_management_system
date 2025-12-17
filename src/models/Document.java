package models;

import java.sql.Timestamp;

/**
 * Document class representing files attached to parcels/transactions
 */
public class Document {

    // Attributes
    private int documentId;
    private DocumentType type;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String fileFormat;
    private Integer parcelId;
    private Integer transactionId;
    private Integer userId;
    private Timestamp uploadedAt;
    private String description;

    // Enum
    public enum DocumentType {
        LAND_TITLE, SALE_DEED, CONTRACT, CERTIFICATE, PLAN, PHOTO, OTHER
    }

    // Constructors
    public Document() {
    }

    public Document(DocumentType type, String fileName, String filePath,
                    String fileFormat, String description) {
        this.type = type;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileFormat = fileFormat;
        this.description = description;
    }

    // Getters and Setters
    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getParcelId() {
        return parcelId;
    }

    public void setParcelId(Integer parcelId) {
        this.parcelId = parcelId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper method
    public String getFileSizeInMB() {
        return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", type=" + type +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + getFileSizeInMB() +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}