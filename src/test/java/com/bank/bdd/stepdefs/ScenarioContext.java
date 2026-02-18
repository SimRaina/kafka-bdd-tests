package com.bank.bdd.stepdefs;

import org.springframework.stereotype.Component;

/**
 * Shared context to pass data between step definitions within a scenario
 */
@Component
public class ScenarioContext {

    private String currentCustomerId;
    private String apiResponseBody;
    private Integer apiResponseStatusCode;

    public String getCurrentCustomerId() {
        return currentCustomerId;
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
    }

    public String getApiResponseBody() {
        return apiResponseBody;
    }

    public void setApiResponseBody(String apiResponseBody) {
        this.apiResponseBody = apiResponseBody;
    }

    public Integer getApiResponseStatusCode() {
        return apiResponseStatusCode;
    }

    public void setApiResponseStatusCode(Integer apiResponseStatusCode) {
        this.apiResponseStatusCode = apiResponseStatusCode;
    }
}
