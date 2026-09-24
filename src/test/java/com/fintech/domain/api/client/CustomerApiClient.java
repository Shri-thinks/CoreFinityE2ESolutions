package com.fintech.domain.api.client;

import com.fintech.domain.models.CustomerDto;
import com.fintech.framework.api.RestClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomerApiClient {

    private static final Logger LOGGER = LogManager.getLogger(CustomerApiClient.class);
    private static final String CUSTOMERS_ENDPOINT = "/api/v1/customers";

    public Response createCustomer(CustomerDto customerDto) {
        LOGGER.info("API: Creating customer: {} {}", customerDto.getFirstName(), customerDto.getLastName());
        return RestClient.post(CUSTOMERS_ENDPOINT, customerDto);
    }

    public Response getCustomerById(String customerId) {
        LOGGER.info("API: Fetching customer by ID: {}", customerId);
        return RestClient.get(CUSTOMERS_ENDPOINT + "/" + customerId);
    }
}
