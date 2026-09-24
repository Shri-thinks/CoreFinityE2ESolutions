package com.fintech.domain.api.client;

import com.fintech.domain.models.AuthorizationRequestDto;
import com.fintech.framework.api.RestClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthorizationApiClient {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationApiClient.class);
    private static final String AUTH_ENDPOINT = "/api/v1/payments/authorize";

    public Response authorizePayment(AuthorizationRequestDto authRequest) {
        LOGGER.info("API: Simulating Authorization for Amount: {} {} at Merchant: {}",
                authRequest.getAmount(), authRequest.getCurrency(), authRequest.getMerchantName());
        return RestClient.post(AUTH_ENDPOINT, authRequest);
    }
}
