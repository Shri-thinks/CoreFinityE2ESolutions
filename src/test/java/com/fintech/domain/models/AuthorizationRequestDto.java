package com.fintech.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationRequestDto {
    private String cardToken;
    private String cardNumber;
    private BigDecimal amount;
    private String currency;
    private String merchantName;
    private String mcc;
    private String cvv;
    private String expiryDate;

    public AuthorizationRequestDto() {}

    public AuthorizationRequestDto(String cardToken, String cardNumber, BigDecimal amount, String currency,
                                   String merchantName, String mcc, String cvv, String expiryDate) {
        this.cardToken = cardToken;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.currency = currency;
        this.merchantName = merchantName;
        this.mcc = mcc;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String cardToken;
        private String cardNumber;
        private BigDecimal amount;
        private String currency;
        private String merchantName;
        private String mcc;
        private String cvv;
        private String expiryDate;

        public Builder cardToken(String cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public Builder mcc(String mcc) {
            this.mcc = mcc;
            return this;
        }

        public Builder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Builder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public AuthorizationRequestDto build() {
            return new AuthorizationRequestDto(cardToken, cardNumber, amount, currency, merchantName, mcc, cvv, expiryDate);
        }
    }

    // Getters and Setters
    public String getCardToken() { return cardToken; }
    public void setCardToken(String cardToken) { this.cardToken = cardToken; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getMcc() { return mcc; }
    public void setMcc(String mcc) { this.mcc = mcc; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
