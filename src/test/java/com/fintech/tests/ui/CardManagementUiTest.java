package com.fintech.tests.ui;

import com.fintech.domain.ui.pages.CardManagementPage;
import com.fintech.framework.utils.FintechDataFactory;
import com.fintech.tests.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Card Management Portal")
public class CardManagementUiTest extends BaseTest {

    @Test(description = "Verify Cardholder can activate card and set secure PIN via Portal UI")
    @Description("Tests card activation flow and PIN setup with UI assertions")
    public void testCardActivationAndPinSetup() {
        String cardId = "CRD-" + System.currentTimeMillis();
        String pin = FintechDataFactory.getRandomPin();

        CardManagementPage cardPage = new CardManagementPage();
        cardPage.load(cardId);

        // Verify initial state
        Assert.assertEquals(cardPage.getCardStatus(), "PENDING_ACTIVATION", "Card should start as PENDING_ACTIVATION");

        // Action: Activate card
        cardPage.clickActivateCard();
        Assert.assertEquals(cardPage.getCardStatus(), "ACTIVE", "Card status badge should transition to ACTIVE");

        // Action: Set PIN
        cardPage.setPin(pin);
        Assert.assertTrue(cardPage.isSuccessToastDisplayed(), "Success notification should appear after setting PIN");
    }

    @Test(description = "Verify Cardholder can freeze an active card to prevent fraud")
    @Description("Tests card security freeze and unfreeze toggles")
    public void testCardFreezeToggle() {
        String cardId = "CRD-" + System.currentTimeMillis();

        CardManagementPage cardPage = new CardManagementPage();
        cardPage.load(cardId);
        cardPage.clickActivateCard();

        // Action: Toggle freeze
        cardPage.toggleFreeze();
        Assert.assertEquals(cardPage.getCardStatus(), "FROZEN", "Card status should update to FROZEN");

        // Action: Unfreeze
        cardPage.toggleFreeze();
        Assert.assertEquals(cardPage.getCardStatus(), "ACTIVE", "Card status should restore to ACTIVE");
    }
}
