package models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Transaction class representing land transactions
 */
public class Transaction {

    // Attributes
    private int transactionId;
    private int parcelId;
    private TransactionType type;
    private Integer previousOwnerId;
    private int newOwnerId;
    private BigDecimal amount;
    private String currency;
    private Date transactionDate;
    private TransactionStatus status;
    private Integer validatingAgentId;
    private Timestamp validationDate;
    private BigDecimal transactionFees;
    private BigDecimal applicableTax;
    private String deedNumber;
    private String notes;
    private Timestamp createdAt;

    // Enums
    public enum TransactionType {
        SALE, PURCHASE, TRANSFER, INHERITANCE, DONATION, EXCHANGE
    }

    public enum TransactionStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    // Constructors
    public Transaction() {
    }

    public Transaction(int parcelId, TransactionType type, Integer previousOwnerId,
                       int newOwnerId, BigDecimal amount, Date transactionDate) {
        this.parcelId = parcelId;
        this.type = type;
        this.previousOwnerId = previousOwnerId;
        this.newOwnerId = newOwnerId;
        this.amount = amount;
        this.currency = "XOF";
        this.transactionDate = transactionDate;
        this.status = TransactionStatus.PENDING;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Integer getPreviousOwnerId() {
        return previousOwnerId;
    }

    public void setPreviousOwnerId(Integer previousOwnerId) {
        this.previousOwnerId = previousOwnerId;
    }

    public int getNewOwnerId() {
        return newOwnerId;
    }

    public void setNewOwnerId(int newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Integer getValidatingAgentId() {
        return validatingAgentId;
    }

    public void setValidatingAgentId(Integer validatingAgentId) {
        this.validatingAgentId = validatingAgentId;
    }

    public Timestamp getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Timestamp validationDate) {
        this.validationDate = validationDate;
    }

    public BigDecimal getTransactionFees() {
        return transactionFees;
    }

    public void setTransactionFees(BigDecimal transactionFees) {
        this.transactionFees = transactionFees;
    }

    public BigDecimal getApplicableTax() {
        return applicableTax;
    }

    public void setApplicableTax(BigDecimal applicableTax) {
        this.applicableTax = applicableTax;
    }

    public String getDeedNumber() {
        return deedNumber;
    }

    public void setDeedNumber(String deedNumber) {
        this.deedNumber = deedNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", parcelId=" + parcelId +
                ", type=" + type +
                ", amount=" + amount + " " + currency +
                ", date=" + transactionDate +
                ", status=" + status +
                '}';
    }
}