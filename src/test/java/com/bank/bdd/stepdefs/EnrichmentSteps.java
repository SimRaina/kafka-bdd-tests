package com.bank.bdd.stepdefs;

import com.bank.jobs.CustomerEnrichmentJob;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnrichmentSteps {

    @Autowired
    CustomerEnrichmentJob enrichmentJob;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Given("valid customer events are processed")
    public void valid_customer_events_are_processed() {
        // This assumes Kafka -> consumer already run in earlier step
        // If not, we see DB directly
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM CUSTOMER",
                Integer.class
        );

        if(count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO CUSTOMER (customer_id, name) VALUES (?, ?)",
                    "1", "John"
            );
        }
        Integer finalCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM CUSTOMER",
                Integer.class
        );

        assertTrue(finalCount > 0, "No base customer data found");
    }

    @When("enrichment job is triggered")
    public void run_enrichment_job() {
        enrichmentJob.run();
    }

    @Then("enriched customer table should have {int} record")
    public void validate_enriched_count(int expected) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM CUSTOMER_ENRICHED",
                Integer.class
        );
        assertEquals(expected, count);
    }

    @And("enriched customer name should be {string}")
    public void enriched_customer_name_should_be(String expectedName) {
        String name = jdbcTemplate.queryForObject(
                "SELECT enriched_name FROM CUSTOMER_ENRICHED WHERE CUSTOMER_ID = 'CUST1'",
                String.class
        );

        assertEquals(expectedName, name);
    }
}
