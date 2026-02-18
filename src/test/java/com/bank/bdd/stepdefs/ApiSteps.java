package com.bank.bdd.stepdefs;

import com.bank.bdd.support.MockApiServer;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiSteps {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private MockApiServer mockApiServer;

    @Given("an API endpoint is available for customer status")
    public void anApiEndpointIsAvailableForCustomerStatus() throws Exception {
        mockApiServer.start();
    }

    @When("customer status payload is posted for customer {string}")
    public void customerStatusPayloadIsPostedForCustomer(String customerId) throws Exception {
        String requestBody = "{\"customerId\":\"" + customerId + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(mockApiServer.getEndpointUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        scenarioContext.setApiResponseBody(response.body());
        scenarioContext.setApiResponseStatusCode(response.statusCode());
        scenarioContext.setCurrentCustomerId(customerId);
    }

    @And("api response is saved to API response table")
    public void apiResponseIsSavedToApiResponseTable() {
        String customerId = scenarioContext.getCurrentCustomerId();
        String responseBody = scenarioContext.getApiResponseBody();

        Map<String, Object> fields = parseApiResponse(responseBody);

        jdbcTemplate.update(
                """
                MERGE INTO CUSTOMER_API_RESPONSE (CUSTOMER_ID, STATUS, SOURCE, RAW_RESPONSE)
                KEY (CUSTOMER_ID)
                VALUES (?, ?, ?, ?)
                """,
                customerId,
                fields.get("status"),
                fields.get("source"),
                responseBody
        );
    }

    @Then("API response should be persisted with status {string} and source {string}")
    public void apiResponseShouldBePersistedWithStatusAndSource(String expectedStatus, String expectedSource) {
        Map<String, Object> dbRow = jdbcTemplate.queryForMap(
                "SELECT STATUS, SOURCE FROM CUSTOMER_API_RESPONSE WHERE CUSTOMER_ID = ?",
                scenarioContext.getCurrentCustomerId()
        );

        assertEquals(200, scenarioContext.getApiResponseStatusCode(), "Unexpected API response status code");
        assertEquals(expectedStatus, dbRow.get("STATUS"));
        assertEquals(expectedSource, dbRow.get("SOURCE"));
    }

    private Map<String, Object> parseApiResponse(String json) {
        return Map.of(
                "status", extractField(json, "status"),
                "source", extractField(json, "source")
        );
    }

    private String extractField(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return "";
        }

        int valueStart = start + marker.length();
        int valueEnd = json.indexOf('"', valueStart);
        if (valueEnd < 0) {
            return "";
        }

        return json.substring(valueStart, valueEnd);
    }
}
