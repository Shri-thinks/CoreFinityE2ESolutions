package com.fintech.framework.driver;

import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver storage using ThreadLocal.
 * Critical for parallel test execution in TestNG (parallel="methods").
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
        // Prevent direct instantiation
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static void unload() {
        DRIVER_THREAD_LOCAL.remove();
    }
}
