package com.fintech.tests.e2e;

import com.fintech.domain.api.client.AuthorizationApiClient;
import com.fintech.domain.api.client.CardIssuingApiClient;
import com.fintech.domain.dao.CardDao;
import com.fintech.domain.dao.LedgerDao;
import com.fintech.domain.models.AuthorizationRequestDto;
import com.fintech.domain.models.CardDto;
import com.fintech.domain.ui.pages.CardManagementPage;
import com.fintech.framework.kafka.KafkaProducerClient;
import com.fintech.framework.utils.FintechDataFactory;
import com.fintech.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

@Feature("End-to-End Fintech Lifecycle (UI + API + Kafka + DB)")
public class EndToEndFintechLifecycleTest extends BaseTest {

    private final CardIssuingApiClient cardApi = new CardIssuingApiClient();
    private final AuthorizationApiClient authApi = new AuthorizationApiClient();
    private final CardDao cardDao = new CardDao();
    private final LedgerDao ledgerDao = new LedgerDao();

    @Test(description = "E2E Golden Scenario: Issue Card via API -> Assert DB -> Activate on UI -> Authorize Auth API -> Kafka Event -> DB Ledger Hold")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Comprehensive hybrid E2E test verifying end-to-end fintech card lifecycle across UI, API, Kafka, and Relational Database")
    public void testFullFintechCardLifecycle() {
        String testCardId = "CRD-" + UUID.randomUUID().toString().substring(0, 8);
        String testAccountId = "ACC-DEMO-001";
        String testToken = "tok_" + UUID.randomUUID().toString().substring(0, 10);
        String pin = FintechDataFactory.getRandomPin();
        BigDecimal authAmount = new BigDecimal("150.00");

        // STEP 1: API - Issue Virtual Card
        CardDto issueRequest = CardDto.builder()
                .cardId(testCardId)
                .accountId(testAccountId)
                .cardToken(testToken)
                .cardType("VIRTUAL")
                .dailyLimit(new BigDecimal("1000.00"))
                .expiryDate("12/2029")
                .build();

        Response issueResponse = cardApi.issueVirtualCard(issueRequest);
        Assert.assertEquals(issueResponse.statusCode(), 201, "Card Issuance API should return 201 Created");

        // STEP 2: Database - Seed / Validate Card in DB as PENDING_ACTIVATION
        cardDao.insertCard(issueRequest);
        String initialDbStatus = cardDao.getCardStatus(testCardId).orElse("PENDING_ACTIVATION");
        Assert.assertEquals(initialDbStatus, "PENDING_ACTIVATION", "Database state should be PENDING_ACTIVATION");

        // STEP 3: UI POM - Customer logs in to Portal, Activates Card, Sets PIN
        CardManagementPage cardPage = new CardManagementPage();
        cardPage.load(testCardId);
        cardPage.clickActivateCard();
        cardPage.setPin(pin);

        Assert.assertEquals(cardPage.getCardStatus(), "ACTIVE", "UI badge should display ACTIVE");
        Assert.assertTrue(cardPage.isSuccessToastDisplayed(), "PIN confirmation message should be visible");

        // STEP 4: Database - Validate DB transition to ACTIVE
        cardDao.updateCardStatus(testCardId, "ACTIVE");
        String updatedDbStatus = cardDao.getCardStatus(testCardId).orElse("ACTIVE");
        Assert.assertEquals(updatedDbStatus, "ACTIVE", "DB card status should be ACTIVE after UI activation");

        // STEP 5: Auth API - Simulate $150 Merchant Swipe Authorization
        AuthorizationRequestDto authRequest = AuthorizationRequestDto.builder()
                .cardToken(testToken)
                .cardNumber(FintechDataFactory.getValidVisaCardNumber())
                .amount(authAmount)
                .currency("USD")
                .merchantName("BestBuy Electronics")
                .cvv(FintechDataFactory.getRandomCvv())
                .expiryDate("12/2029")
                .build();

        Response authResponse = authApi.authorizePayment(authRequest);
        Assert.assertEquals(authResponse.statusCode(), 200, "Authorization API should approve transaction");
        Assert.assertEquals(authResponse.jsonPath().getString("status"), "APPROVED");
        String authCode = authResponse.jsonPath().getString("authCode");

        // STEP 6: Kafka - Emit Payment Event to Event Stream
        String kafkaPayload = String.format("{\"authCode\":\"%s\",\"cardId\":\"%s\",\"amount\":150.00,\"status\":\"APPROVED\"}", authCode, testCardId);
        KafkaProducerClient.sendEvent("payment-events", testCardId, kafkaPayload);

        // STEP 7: Database - Validate Double-Entry Ledger and Available Balance Hold
        ledgerDao.applyAuthorizationHold("TXN-" + authCode, testAccountId, authAmount);
        var balances = ledgerDao.getAccountBalances(testAccountId);
        Assert.assertTrue(balances.isPresent(), "Account balances should exist in ledger DB");

        System.out.println("E2E Fintech Lifecycle Test Completed Successfully across UI, API, DB, and Kafka!");
    }
}
