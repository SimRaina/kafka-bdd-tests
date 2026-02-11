package com.bank.bdd.stepdefs;

import io.cucumber.java.en.Then;
import com.bank.bdd.support.H2Database;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Then("customer {string} should exist in database")
    public void validateCustomer(String customerId) throws Exception {
        // Use the customer ID from scenario context instead of the parameter
        // This allows us to validate the dynamically generated customer ID
        String actualCustomerId = scenarioContext.getCurrentCustomerId();

        await()
                .atMost(10, SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    try(Connection conn = H2Database.getConnection()) {
                        PreparedStatement ps =
                                conn.prepareStatement(
                                        "SELECT COUNT(*) FROM CUSTOMER WHERE CUSTOMER_ID = ?"
                                );

                        ps.setString(1, actualCustomerId);

                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        System.out.println("************CUSTOMER RECORD IS******* " + rs.getInt(1));
                        assertEquals(1, rs.getInt(1), "Customer not found in DB");
                    }
                });
    }
}
