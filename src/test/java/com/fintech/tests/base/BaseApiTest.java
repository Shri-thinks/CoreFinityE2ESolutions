package com.fintech.tests.base;

import com.fintech.framework.config.ConfigFactory;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        RestAssured.baseURI = ConfigFactory.getConfig().baseApiUrl();
    }
}
