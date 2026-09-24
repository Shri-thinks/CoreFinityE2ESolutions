package com.fintech.domain.ui.pages;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.ui.BasePage;
import org.openqa.selenium.By;

public class ParaBankLoginPage extends BasePage {

    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("input[value='Log In']");
    private final By accountsOverviewHeader = By.xpath("//h1[contains(text(),'Accounts Overview')]");
    private final By errorMessage = By.cssSelector("p.error");

    public ParaBankLoginPage open() {
        navigateTo(ConfigFactory.getConfig().baseUiUrl() + "/index.htm");
        return this;
    }

    public ParaBankLoginPage enterUsername(String username) {
        enterText(usernameInput, username);
        return this;
    }

    public ParaBankLoginPage enterPassword(String password) {
        enterText(passwordInput, password);
        return this;
    }

    public ParaBankLoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    public boolean isLoginSuccessful() {
        return isElementDisplayed(accountsOverviewHeader);
    }

    public String getErrorMessage() {
        return getElementText(errorMessage);
    }
}
