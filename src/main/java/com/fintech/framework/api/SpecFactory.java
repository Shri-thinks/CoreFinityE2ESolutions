package com.fintech.framework.api;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.config.FrameworkConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class SpecFactory {

    private SpecFactory() {}

    public static RequestSpecification getRequestSpec() {
        FrameworkConfig config = ConfigFactory.getConfig();
        return new RequestSpecBuilder()
                .setBaseUri(config.baseApiUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", config.authToken())
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .log(LogDetail.BODY)
                .build();
    }

    public static ResponseSpecification getResponseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.STATUS)
                .log(LogDetail.BODY)
                .build();
    }
}
