package com.fintech.domain.ui.pages;

import com.fintech.framework.ui.BasePage;
import org.openqa.selenium.By;

public class CardManagementPage extends BasePage {

    // Web Locators
    private final By cardStatusBadge = By.id("card-status-badge");
    private final By activateCardBtn = By.id("btn-activate-card");
    private final By pinInput = By.id("input-card-pin");
    private final By confirmPinInput = By.id("input-confirm-pin");
    private final By submitPinBtn = By.id("btn-submit-pin");
    private final By dailyLimitInput = By.id("input-daily-limit");
    private final By updateLimitBtn = By.id("btn-update-limit");
    private final By freezeToggleBtn = By.id("btn-toggle-freeze");
    private final By successToast = By.cssSelector(".toast-success, .alert-success");

    public CardManagementPage load(String cardId) {
        logger.info("Loading Card Management Portal for Card ID: {}", cardId);
        // Can be pointed to local mock portal or demo UI
        navigateTo("data:text/html;charset=utf-8," +
                "<html><head><title>Card Portal</title></head><body>" +
                "<h1>Card Management</h1>" +
                "<span id='card-status-badge'>PENDING_ACTIVATION</span>" +
                "<button id='btn-activate-card' onclick=\"document.getElementById('card-status-badge').innerText='ACTIVE'\">Activate Card</button>" +
                "<input id='input-card-pin' type='password' placeholder='PIN'/>" +
                "<input id='input-confirm-pin' type='password' placeholder='Confirm PIN'/>" +
                "<button id='btn-submit-pin' onclick=\"document.getElementById('toast').innerText='PIN set successfully'\">Set PIN</button>" +
                "<input id='input-daily-limit' type='number' value='1000'/>" +
                "<button id='btn-update-limit'>Update Limit</button>" +
                "<button id='btn-toggle-freeze' onclick=\"var b=document.getElementById('card-status-badge'); b.innerText = (b.innerText==='ACTIVE'?'FROZEN':'ACTIVE');\">Toggle Freeze</button>" +
                "<div id='toast' class='toast-success'></div>" +
                "</body></html>");
        return this;
    }

    public CardManagementPage clickActivateCard() {
        click(activateCardBtn);
        return this;
    }

    public CardManagementPage setPin(String pin) {
        enterText(pinInput, pin);
        enterText(confirmPinInput, pin);
        click(submitPinBtn);
        return this;
    }

    public CardManagementPage setDailyLimit(String amount) {
        enterText(dailyLimitInput, amount);
        click(updateLimitBtn);
        return this;
    }

    public CardManagementPage toggleFreeze() {
        click(freezeToggleBtn);
        return this;
    }

    public String getCardStatus() {
        return getElementText(cardStatusBadge);
    }

    public boolean isSuccessToastDisplayed() {
        return isElementDisplayed(successToast);
    }
}
