package com.fintech.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationResponseDto {
    private String authorizationId;
    private String authCode;
    private String status;
    private String responseCode;
    private BigDecimal authorizedAmount;
    private String currency;
    private String merchant;
    private String timestamp;

    public AuthorizationResponseDto() {}

    public AuthorizationResponseDto(String authorizationId, String authCode, String status, String responseCode,
                                    BigDecimal authorizedAmount, String currency, String merchant, String timestamp) {
        this.authorizationId = authorizationId;
        this.authCode = authCode;
        this.status = status;
        this.responseCode = responseCode;
        this.authorizedAmount = authorizedAmount;
        this.currency = currency;
        this.merchant = merchant;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String authorizationId;
        private String authCode;
        private String status;
        private String responseCode;
        private BigDecimal authorizedAmount;
        private String currency;
        private String merchant;
        private String timestamp;

        public Builder authorizationId(String authorizationId) {
            this.authorizationId = authorizationId;
            return this;
        }

        public Builder authCode(String authCode) {
            this.authCode = authCode;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder responseCode(String responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder authorizedAmount(BigDecimal authorizedAmount) {
            this.authorizedAmount = authorizedAmount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder merchant(String merchant) {
            this.merchant = merchant;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuthorizationResponseDto build() {
            return new AuthorizationResponseDto(authorizationId, authCode, status, responseCode, authorizedAmount, currency, merchant, timestamp);
        }
    }

    // Getters and Setters
    public String getAuthorizationId() { return authorizationId; }
    public void setAuthorizationId(String authorizationId) { this.authorizationId = authorizationId; }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }

    public BigDecimal getAuthorizedAmount() { return authorizedAmount; }
    public void setAuthorizedAmount(BigDecimal authorizedAmount) { this.authorizedAmount = authorizedAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
