package com.fintech.framework.driver;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;

public final class OptionsManager {

    private OptionsManager() {}

    public static ChromeOptions getChromeOptions(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    public static FirefoxOptions getFirefoxOptions(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless) {
            options.addArguments("-headless");
        }
        return options;
    }

    public static EdgeOptions getEdgeOptions(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
}
