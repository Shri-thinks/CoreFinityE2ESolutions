package com.fintech.framework.ui;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        int timeout = ConfigFactory.getConfig().explicitTimeoutSeconds();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    protected void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        logger.info("Clicking on element: {}", locator);
        try {
            waitForClickability(locator).click();
        } catch (ElementClickInterceptedException e) {
            logger.warn("Element click intercepted on {}. Retrying with JavaScript click.", locator);
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void enterText(By locator, String text) {
        logger.info("Entering text into element: {}", locator);
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getElementText(By locator) {
        String text = waitForVisibility(locator).getText();
        logger.info("Retrieved text '{}' from element: {}", text, locator);
        return text;
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }
}
