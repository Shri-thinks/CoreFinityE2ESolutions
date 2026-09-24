package com.fintech.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDto {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String kycStatus;
    private String accountId;
    private String createdAt;
}
