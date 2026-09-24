package com.fintech.domain.ui.pages;

import com.fintech.framework.config.ConfigFactory;
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
    private final By successToast = By.id("toast");

    public CardManagementPage load(String cardId) {
        logger.info("Loading Card Management Portal for Card ID: {}", cardId);
        navigateTo(ConfigFactory.getConfig().baseApiUrl() + "/card-portal");
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
