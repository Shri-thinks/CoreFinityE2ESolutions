package com.fintech.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDto {
    private String cardId;
    private String accountId;
    private String cardType;
    private String maskedPan;
    private String cardNumber;
    private String cardToken;
    private String status;
    private String pin;
    private BigDecimal dailyLimit;
    private String expiryDate;
    private String cvv;

    public CardDto() {}

    public CardDto(String cardId, String accountId, String cardType, String maskedPan, String cardNumber,
                   String cardToken, String status, String pin, BigDecimal dailyLimit, String expiryDate, String cvv) {
        this.cardId = cardId;
        this.accountId = accountId;
        this.cardType = cardType;
        this.maskedPan = maskedPan;
        this.cardNumber = cardNumber;
        this.cardToken = cardToken;
        this.status = status;
        this.pin = pin;
        this.dailyLimit = dailyLimit;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String cardId;
        private String accountId;
        private String cardType;
        private String maskedPan;
        private String cardNumber;
        private String cardToken;
        private String status;
        private String pin;
        private BigDecimal dailyLimit;
        private String expiryDate;
        private String cvv;

        public Builder cardId(String cardId) {
            this.cardId = cardId;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder maskedPan(String maskedPan) {
            this.maskedPan = maskedPan;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder cardToken(String cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder pin(String pin) {
            this.pin = pin;
            return this;
        }

        public Builder dailyLimit(BigDecimal dailyLimit) {
            this.dailyLimit = dailyLimit;
            return this;
        }

        public Builder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public CardDto build() {
            return new CardDto(cardId, accountId, cardType, maskedPan, cardNumber, cardToken, status, pin, dailyLimit, expiryDate, cvv);
        }
    }

    // Getters and Setters
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getMaskedPan() { return maskedPan; }
    public void setMaskedPan(String maskedPan) { this.maskedPan = maskedPan; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardToken() { return cardToken; }
    public void setCardToken(String cardToken) { this.cardToken = cardToken; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
