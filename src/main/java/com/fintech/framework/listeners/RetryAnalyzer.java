package com.fintech.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOGGER = LogManager.getLogger(RetryAnalyzer.class);
    private int counter = 0;
    private static final int MAX_RETRY_COUNT = 1;

    @Override
    public boolean retry(ITestResult result) {
        if (counter < MAX_RETRY_COUNT) {
            counter++;
            LOGGER.warn("Retrying test '{}' - Attempt {}/{}", result.getName(), counter, MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
