package com.fintech.framework.api;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class RestClient {

    private static final Logger LOGGER = LogManager.getLogger(RestClient.class);

    private RestClient() {}

    public static Response get(String endpoint, Map<String, ?> queryParams) {
        LOGGER.info("Executing GET request to endpoint: {}", endpoint);
        var request = given().spec(SpecFactory.getRequestSpec());
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        return request.when().get(endpoint).then().spec(SpecFactory.getResponseSpec()).extract().response();
    }

    public static Response get(String endpoint) {
        return get(endpoint, null);
    }

    public static Response post(String endpoint, Object payload) {
        LOGGER.info("Executing POST request to endpoint: {}", endpoint);
        return given()
                .spec(SpecFactory.getRequestSpec())
                .body(payload)
                .when()
                .post(endpoint)
                .then()
                .spec(SpecFactory.getResponseSpec())
                .extract()
                .response();
    }

    public static Response put(String endpoint, Object payload) {
        LOGGER.info("Executing PUT request to endpoint: {}", endpoint);
        return given()
                .spec(SpecFactory.getRequestSpec())
                .body(payload)
                .when()
                .put(endpoint)
                .then()
                .spec(SpecFactory.getResponseSpec())
                .extract()
                .response();
    }

    public static Response delete(String endpoint) {
        LOGGER.info("Executing DELETE request to endpoint: {}", endpoint);
        return given()
                .spec(SpecFactory.getRequestSpec())
                .when()
                .delete(endpoint)
                .then()
                .spec(SpecFactory.getResponseSpec())
                .extract()
                .response();
    }
}
