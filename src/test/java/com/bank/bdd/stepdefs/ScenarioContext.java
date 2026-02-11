package com.bank.bdd.stepdefs;

import org.springframework.stereotype.Component;

/**
 * Shared context to pass data between step definitions within a scenario
 */
@Component
public class ScenarioContext {

    private String currentCustomerId;

    public String getCurrentCustomerId() {
        return currentCustomerId;
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
    }
}
