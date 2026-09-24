package com.fintech.framework.driver;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.config.FrameworkConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public final class DriverFactory {

    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static void initDriver() {
        FrameworkConfig config = ConfigFactory.getConfig();
        String browser = config.browser().toLowerCase();
        boolean isHeadless = config.headless();
        String mode = config.executionMode().toLowerCase();

        LOGGER.info("Initializing WebDriver - Browser: {}, Headless: {}, ExecutionMode: {}", browser, isHeadless, mode);

        WebDriver driver;
        if ("grid".equalsIgnoreCase(mode)) {
            driver = createRemoteDriver(browser, isHeadless, config.seleniumGridUrl());
        } else {
            driver = createLocalDriver(browser, isHeadless);
        }

        driver.manage().window().maximize();
        DriverManager.setDriver(driver);
    }

    private static WebDriver createLocalDriver(String browser, boolean isHeadless) {
        return switch (browser) {
            case "firefox" -> new FirefoxDriver(OptionsManager.getFirefoxOptions(isHeadless));
            case "edge" -> new EdgeDriver(OptionsManager.getEdgeOptions(isHeadless));
            default -> new ChromeDriver(OptionsManager.getChromeOptions(isHeadless));
        };
    }

    private static WebDriver createRemoteDriver(String browser, boolean isHeadless, String gridUrl) {
        try {
            URL url = new URL(gridUrl);
            return switch (browser) {
                case "firefox" -> new RemoteWebDriver(url, OptionsManager.getFirefoxOptions(isHeadless));
                case "edge" -> new RemoteWebDriver(url, OptionsManager.getEdgeOptions(isHeadless));
                default -> new RemoteWebDriver(url, OptionsManager.getChromeOptions(isHeadless));
            };
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid Selenium Grid URL: {}", gridUrl, e);
            throw new IllegalArgumentException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    public static void quitDriver() {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            LOGGER.info("Quitting WebDriver for thread: {}", Thread.currentThread().getName());
            driver.quit();
            DriverManager.unload();
        }
    }
}
