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
public class AuthorizationResponseDto {
    private String authorizationId;
    private String authCode;
    private String status; // APPROVED, DECLINED, BLOCKED
    private String responseCode;
    private BigDecimal authorizedAmount;
    private String currency;
    private String merchant;
    private String timestamp;
}
