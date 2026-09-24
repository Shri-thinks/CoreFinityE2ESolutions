package com.fintech.tests.api;

import com.fintech.domain.api.client.AuthorizationApiClient;
import com.fintech.domain.api.client.CardIssuingApiClient;
import com.fintech.domain.api.client.CustomerApiClient;
import com.fintech.domain.models.AuthorizationRequestDto;
import com.fintech.domain.models.CardDto;
import com.fintech.domain.models.CustomerDto;
import com.fintech.framework.utils.FintechDataFactory;
import com.fintech.tests.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

@Feature("Fintech Core Banking REST APIs")
public class CardIssuanceApiTest extends BaseApiTest {

    private final CustomerApiClient customerApi = new CustomerApiClient();
    private final CardIssuingApiClient cardApi = new CardIssuingApiClient();
    private final AuthorizationApiClient authApi = new AuthorizationApiClient();

    @Test(description = "Verify Customer Creation and KYC Status via API")
    @Description("Validates that a new customer is provisioned with VERIFIED KYC status")
    public void testCustomerCreation() {
        CustomerDto customer = CustomerDto.builder()
                .firstName(FintechDataFactory.getRandomFirstName())
                .lastName(FintechDataFactory.getRandomLastName())
                .email(FintechDataFactory.getRandomEmail())
                .build();

        Response response = customerApi.createCustomer(customer);
        Assert.assertEquals(response.statusCode(), 201, "Expected 201 Created for new customer");
        Assert.assertNotNull(response.jsonPath().getString("customerId"), "Customer ID must not be null");
        Assert.assertEquals(response.jsonPath().getString("kycStatus"), "VERIFIED");
    }

    @Test(description = "Verify Virtual Card Issuance via REST API")
    @Description("Validates that issuing a virtual card returns a valid token and PENDING_ACTIVATION status")
    public void testVirtualCardIssuance() {
        CardDto cardRequest = CardDto.builder()
                .accountId("ACC-DEMO-001")
                .cardType("VIRTUAL")
                .dailyLimit(new BigDecimal("1500.00"))
                .build();

        Response response = cardApi.issueVirtualCard(cardRequest);
        Assert.assertEquals(response.statusCode(), 201, "Expected 201 Created for card issuance");
        Assert.assertNotNull(response.jsonPath().getString("cardId"));
        Assert.assertNotNull(response.jsonPath().getString("cardToken"));
        Assert.assertEquals(response.jsonPath().getString("status"), "PENDING_ACTIVATION");
    }

    @Test(description = "Verify Payment Authorization Transaction API")
    @Description("Simulates merchant card swipe and asserts authorization approval")
    public void testPaymentAuthorization() {
        AuthorizationRequestDto authRequest = AuthorizationRequestDto.builder()
                .cardToken("tok_visa_card_enc_998877")
                .cardNumber(FintechDataFactory.getValidVisaCardNumber())
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .merchantName("Amazon Web Services")
                .cvv(FintechDataFactory.getRandomCvv())
                .expiryDate(FintechDataFactory.getRandomExpiryDate())
                .build();

        Response response = authApi.authorizePayment(authRequest);
        Assert.assertEquals(response.statusCode(), 200, "Expected 200 OK for payment authorization");
        Assert.assertEquals(response.jsonPath().getString("status"), "APPROVED");
        Assert.assertNotNull(response.jsonPath().getString("authCode"));
    }
}
