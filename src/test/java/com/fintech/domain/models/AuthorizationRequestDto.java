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
public class AuthorizationRequestDto {
    private String cardToken;
    private String cardNumber;
    private BigDecimal amount;
    private String currency;
    private String merchantName;
    private String mcc; // Merchant Category Code
    private String cvv;
    private String expiryDate;
}
