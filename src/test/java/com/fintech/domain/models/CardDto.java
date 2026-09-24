package com.fintech.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDto {
    private String cardId;
    private String accountId;
    private String cardType; // VIRTUAL or PHYSICAL
    private String maskedPan;
    private String cardNumber;
    private String cardToken;
    private String status; // PENDING_ACTIVATION, ACTIVE, FROZEN, TERMINATED
    private String pin;
    private BigDecimal dailyLimit;
    private String expiryDate;
    private String cvv;
}
