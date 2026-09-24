package com.fintech.framework.observability;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.driver.DriverManager;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class GrafanaTestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(GrafanaTestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info("================ STARTING TEST SUITE: {} ================", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("================ FINISHED TEST SUITE: {} ================", context.getName());
        InfluxDbMetricsPublisher.close();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info(">>> START TEST: {}#{}", result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        LOGGER.info("<<< TEST PASSED: {} ({} ms)", result.getMethod().getMethodName(), duration);
        publishMetric(result, "PASSED", duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        LOGGER.error("<<< TEST FAILED: {} ({} ms). Reason: {}", result.getMethod().getMethodName(), duration, result.getThrowable().getMessage());
        publishMetric(result, "FAILED", duration);
        captureAndAttachScreenshot();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        LOGGER.warn("<<< TEST SKIPPED: {}", result.getMethod().getMethodName());
        publishMetric(result, "SKIPPED", duration);
    }

    private void publishMetric(ITestResult result, String status, long duration) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        String env = ConfigFactory.getConfig().environment();
        InfluxDbMetricsPublisher.publishTestMetric(testName, className, status, duration, env);
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    private byte[] captureAndAttachScreenshot() {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                LOGGER.warn("Could not capture screenshot on failure: {}", e.getMessage());
            }
        }
        return new byte[0];
    }
}
