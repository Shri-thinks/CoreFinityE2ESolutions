package com.fintech.domain.api.client;

import com.fintech.domain.models.CardDto;
import com.fintech.framework.api.RestClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CardIssuingApiClient {

    private static final Logger LOGGER = LogManager.getLogger(CardIssuingApiClient.class);
    private static final String CARDS_ISSUE_ENDPOINT = "/api/v1/cards/issue";
    private static final String CARDS_ENDPOINT = "/api/v1/cards";

    public Response issueVirtualCard(CardDto cardDto) {
        LOGGER.info("API: Issuing Virtual Card for Account ID: {}", cardDto.getAccountId());
        return RestClient.post(CARDS_ISSUE_ENDPOINT, cardDto);
    }

    public Response getCardById(String cardId) {
        LOGGER.info("API: Fetching Card details for Card ID: {}", cardId);
        return RestClient.get(CARDS_ENDPOINT + "/" + cardId);
    }

    public Response freezeCard(String cardId) {
        LOGGER.info("API: Freezing Card ID: {}", cardId);
        return RestClient.put(CARDS_ENDPOINT + "/" + cardId + "/freeze", null);
    }
}
