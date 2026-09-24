package com.fintech.tests.base;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.mock.EmbeddedMockBankingServer;
import io.restassured.RestAssured;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        EmbeddedMockBankingServer.startIfNeeded();
        RestAssured.baseURI = ConfigFactory.getConfig().baseApiUrl();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        EmbeddedMockBankingServer.stop();
    }
}
