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
public class PaymentEventMessageDto {
    private String eventId;
    private String eventType; // PAYMENT_AUTHORIZED, PAYMENT_SETTLED, CARD_FROZEN
    private String cardId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String timestamp;
}
