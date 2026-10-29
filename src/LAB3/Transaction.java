package LAB3;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;      // VARCHAR in the database
    private String referenceId;        // e.g. "DEP-001", so it must be a String
    private double amount;
    private String transactionType;
    private LocalDateTime timestamp;
    private String accountId;

    public Transaction(String transactionId, String referenceId, double amount, String transactionType,
                       LocalDateTime timestamp, String accountId) {
        this.transactionId = transactionId;
        this.referenceId = referenceId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        this.accountId = accountId;
    }

    public Transaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "referenceId='" + referenceId + '\'' +
                ", type='" + transactionType + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", accountId='" + accountId + '\'' +
                '}';
    }
}
